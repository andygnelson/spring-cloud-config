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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.function.Supplier;

import com.oracle.bmc.Region;
import com.oracle.bmc.auth.SimpleAuthenticationDetailsProvider;
import com.oracle.bmc.auth.SimpleAuthenticationDetailsProvider.SimpleAuthenticationDetailsProviderBuilder;
import com.oracle.bmc.objectstorage.ObjectStorageClient;

import org.springframework.cloud.config.server.config.ConfigServerProperties;
import org.springframework.util.StringUtils;

public class OciObjectStorageEnvironmentRepositoryFactory implements
		EnvironmentRepositoryFactory<OciObjectStorageEnvironmentRepository, OciObjectStorageEnvironmentProperties> {

	final private ConfigServerProperties server;

	public OciObjectStorageEnvironmentRepositoryFactory(ConfigServerProperties server) {
		this.server = server;
	}

	@Override
	public OciObjectStorageEnvironmentRepository build(OciObjectStorageEnvironmentProperties environmentProperties)
			throws Exception {
		Supplier<InputStream> pkSupplier = () -> {
			try {
				return (InputStream) new FileInputStream(new File(environmentProperties.getPrivateKeyPath()));
			}
			catch (FileNotFoundException e) {
				return null;
			}
		};

		if (pkSupplier.get() == null) {
			throw new FileNotFoundException("Path to private key file does not exist.");
		}

		SimpleAuthenticationDetailsProviderBuilder simpleAuthProviderBuilder = SimpleAuthenticationDetailsProvider
				.builder();
		simpleAuthProviderBuilder.userId(environmentProperties.getUserId())
				.fingerprint(environmentProperties.getFingerprint())
				.region(Region.fromRegionId(environmentProperties.getRegion()))
				.tenantId(environmentProperties.getTenantId()).privateKeySupplier(pkSupplier);
		if (StringUtils.hasText(environmentProperties.getPassPhrase())) {
			simpleAuthProviderBuilder.passPhrase(environmentProperties.getPassPhrase());
		}
		SimpleAuthenticationDetailsProvider authenticationDetailsProvider = simpleAuthProviderBuilder.build();

		ObjectStorageClient.Builder builder = ObjectStorageClient.builder();
		builder.region(environmentProperties.getRegion());
		if (StringUtils.hasText(environmentProperties.getEndpoint())) {
			builder.endpoint(environmentProperties.getEndpoint());
		}
		ObjectStorageClient objectStorageClient = builder.build(authenticationDetailsProvider);

		return new OciObjectStorageEnvironmentRepository(objectStorageClient, environmentProperties.getBucket(),
				environmentProperties.getNamespace(), environmentProperties.getSearchPaths(), server);
	}

}
