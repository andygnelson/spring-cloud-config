/*
 * Copyright 2013-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.config.server.environment;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import com.oracle.bmc.objectstorage.ObjectStorageClient;
import com.oracle.bmc.objectstorage.requests.GetObjectRequest;
import com.oracle.bmc.objectstorage.responses.GetObjectResponse;

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.cloud.config.environment.Environment;
import org.springframework.cloud.config.environment.PropertySource;
import org.springframework.cloud.config.server.config.ConfigServerProperties;
import org.springframework.core.Ordered;
import org.springframework.core.io.InputStreamResource;
import org.springframework.util.StringUtils;

public class OciObjectStorageEnvironmentRepository implements EnvironmentRepository, Ordered, SearchPathLocator {

	protected int order = Ordered.LOWEST_PRECEDENCE;

	private static final String PATH_SEPARATOR = "/";

	private final ObjectStorageClient objectStorageClient;

	private final String namespace;

	private final String bucketName;

	private final String[] searchPaths;

	private final ConfigServerProperties serverProperties;

	public OciObjectStorageEnvironmentRepository(ObjectStorageClient objectStorageClient, String bucketName,
			String namespace, String[] searchPaths, ConfigServerProperties server) {
		this.objectStorageClient = objectStorageClient;
		this.bucketName = bucketName;
		this.namespace = namespace;
		this.searchPaths = searchPaths;
		this.serverProperties = server;
	}

	@Override
	public int getOrder() {
		return this.order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	@Override
	public Environment findOne(String application, String profile, String label) {
		String[] profiles = StringUtils.commaDelimitedListToStringArray(profile);
		List<String> scrubbedProfiles = scrubProfiles(profiles);

		List<String> keys = findKeys(application, scrubbedProfiles, label);
		// System.out.println(keys.toString());

		final Environment environment = new Environment(application, profiles, label, null, null);

		for (String key : keys) {
			OciObjectStorageConfigFile configFile = getBucketConfigFile(key);
			if (configFile != null) {
				environment.setVersion(configFile.getVersion());

				final Properties config = configFile.read();
				config.putAll(serverProperties.getOverrides());
				StringBuilder propertySourceName = new StringBuilder().append("oci-objs://").append(this.bucketName)
						.append(PATH_SEPARATOR).append(key);
				environment.add(new PropertySource(propertySourceName.toString(), config));
			}
		}

		return environment;
	}

	private List<String> scrubProfiles(String[] profiles) {
		List<String> scrubbedProfiles = new ArrayList<>(Arrays.asList(profiles));
		scrubbedProfiles.remove("default");
		return scrubbedProfiles;
	}

	private List<String> findKeys(String application, List<String> profiles, String label) {
		List<String> keys = new ArrayList<>();

		List<String> apps = new ArrayList<>();
		apps.add(serverProperties.getDefaultApplicationName());
		if (!application.equals(serverProperties.getDefaultApplicationName())) {
			Collections.addAll(apps, StringUtils.commaDelimitedListToStringArray(application));
		}

		// Search for application, application-profile, namedApp, and namedApp-profile in
		// the bucket with profiles taking precedence
		for (String app : apps) {
			keys.add(app);
		}
		for (String app : apps) {
			for (String profile : profiles) {
				keys.add(app + "-" + profile);
			}
		}

		// Search all of the above keys under each additonal search path beyond the root
		// folder
		for (String searchPath : searchPaths) {
			boolean searchPathProcessed = false;
			ArrayList<String> extrapolatedSearchPaths = new ArrayList<String>();

			// First add an additional search path per extraploated profile
			ArrayList<String> profileExtraplolatedSearchPaths = new ArrayList<String>();
			if (searchPath.contains("{profile}") && profiles.size() > 0) {
				for (String profile : profiles) {
					String placeholderFreeSearchPath = searchPath;
					placeholderFreeSearchPath = placeholderFreeSearchPath.replace("{profile}", profile);
					profileExtraplolatedSearchPaths.add(placeholderFreeSearchPath);
				}
				searchPathProcessed = true;
			}
			extrapolatedSearchPaths.addAll(profileExtraplolatedSearchPaths);

			// Next add an addtional search path per extraploated profile, per application
			ArrayList<String> appExtraploatedSearchPaths = new ArrayList<String>();
			if (searchPath.contains("{application}") && apps.size() > 0) {
				for (String app : apps) {
					if (profileExtraplolatedSearchPaths.size() > 0) {
						for (String pExSearchPath : profileExtraplolatedSearchPaths) {
							String placeholderFreeSearchPath = pExSearchPath;
							placeholderFreeSearchPath = placeholderFreeSearchPath.replace("{application}", app);
							appExtraploatedSearchPaths.add(placeholderFreeSearchPath);
						}
					}
					else {
						String placeholderFreeSearchPath = searchPath;
						placeholderFreeSearchPath = placeholderFreeSearchPath.replace("{application}", app);
						appExtraploatedSearchPaths.add(placeholderFreeSearchPath);
					}
				}
				searchPathProcessed = true;
			}
			extrapolatedSearchPaths.addAll(appExtraploatedSearchPaths);

			// Add in any search paths that were not processed by virtue of substitution
			if (!searchPathProcessed) {
				extrapolatedSearchPaths.add(searchPath);
			}
			// Finally, iterate over the list and sub label as needed
			for (String exSearchPath : extrapolatedSearchPaths) {
				String placeholderFreeSearchPath = exSearchPath;
				if (label != null) {
					placeholderFreeSearchPath = placeholderFreeSearchPath.replace("{label}", label);
				}
				for (String app : apps) {
					keys.add(placeholderFreeSearchPath + PATH_SEPARATOR + app);
				}
				for (String app : apps) {
					for (String profile : profiles) {
						keys.add(placeholderFreeSearchPath + PATH_SEPARATOR + app + "-" + profile);
					}
				}
			}
		}

		Collections.reverse(keys);
		return keys;
	}

	private OciObjectStorageConfigFile getBucketConfigFile(String keyPrefix) {
		try {
			final GetObjectResponse getObjectResponse = getObject(keyPrefix + ".properties");
			return new PropertyOciObjectStorageConfigFile(getObjectResponse.getVersionId(),
					getObjectResponse.getInputStream());
		}
		catch (Exception eProperties) {
			try {
				final GetObjectResponse getObjectResponse = getObject(keyPrefix + ".yml");
				return new YamlOciObjectStorageConfigFile(getObjectResponse.getVersionId(),
						getObjectResponse.getInputStream());
			}
			catch (Exception eYml) {
				try {
					final GetObjectResponse getObjectResponse = getObject(keyPrefix + ".yaml");
					return new YamlOciObjectStorageConfigFile(getObjectResponse.getVersionId(),
							getObjectResponse.getInputStream());
				}
				catch (Exception eYaml) {
					try {
						final GetObjectResponse getObjectResponse = getObject(keyPrefix + ".json");
						return new JsonOciObjectStorageConfigFile(getObjectResponse.getVersionId(),
								getObjectResponse.getInputStream());
					}
					catch (Exception eJson) {
						return null;
					}
				}
			}
		}
	}

	private GetObjectResponse getObject(String key) throws Exception {
		return objectStorageClient.getObject(
				GetObjectRequest.builder().namespaceName(namespace).bucketName(bucketName).objectName(key).build());
	}

	@Override
	public Locations getLocations(String application, String profile, String label) {
		String baseLocation = "ociobj://" + bucketName + PATH_SEPARATOR + application;
		return new Locations(application, profile, label, null, new String[] { baseLocation });
	}

}

abstract class OciObjectStorageConfigFile {

	private final String version;

	protected OciObjectStorageConfigFile(String version) {
		this.version = version;
	}

	String getVersion() {
		return version;
	}

	abstract Properties read();

}

class PropertyOciObjectStorageConfigFile extends OciObjectStorageConfigFile {

	final InputStream inputStream;

	PropertyOciObjectStorageConfigFile(String version, InputStream inputStream) {
		super(version);
		this.inputStream = inputStream;
	}

	@Override
	public Properties read() {
		Properties props = new Properties();
		try (InputStream in = inputStream) {
			props.load(in);
		}
		catch (IOException e) {
			throw new IllegalStateException("Cannot load environment", e);
		}
		return props;
	}

}

class YamlOciObjectStorageConfigFile extends OciObjectStorageConfigFile {

	final InputStream inputStream;

	YamlOciObjectStorageConfigFile(String version, InputStream inputStream) {
		super(version);
		this.inputStream = inputStream;
	}

	@Override
	public Properties read() {
		final YamlPropertiesFactoryBean yaml = new YamlPropertiesFactoryBean();
		try (InputStream in = inputStream) {
			yaml.setResources(new InputStreamResource(in));
			return yaml.getObject();
		}
		catch (IOException e) {
			throw new IllegalStateException("Cannot load environment", e);
		}
	}

}

class JsonOciObjectStorageConfigFile extends YamlOciObjectStorageConfigFile {

	// YAML is a superset of JSON, which means you can parse JSON with a YAML parser

	JsonOciObjectStorageConfigFile(String version, InputStream inputStream) {
		super(version, inputStream);
	}

}
