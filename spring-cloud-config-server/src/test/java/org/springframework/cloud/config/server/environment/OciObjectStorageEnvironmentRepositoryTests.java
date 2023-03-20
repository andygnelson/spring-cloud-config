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

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import com.oracle.bmc.objectstorage.ObjectStorageClient;
import com.oracle.bmc.objectstorage.requests.GetObjectRequest;
import com.oracle.bmc.objectstorage.responses.GetObjectResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;

import org.springframework.cloud.config.environment.Environment;
import org.springframework.cloud.config.server.config.ConfigServerProperties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OciObjectStorageEnvironmentRepositoryTests {

	ObjectStorageClient mockStorageClient = mock(ObjectStorageClient.class);

	ConfigServerProperties mockServerProps = mock(ConfigServerProperties.class);

	OciObjectStorageEnvironmentRepository envRepository;

	@BeforeEach
	public void init() {
		when(mockServerProps.getDefaultApplicationName()).thenReturn("application");
	}

	// Tests for foo-bar in all formats
	@Test
	public void testSingleAppProfilePropsFile() {
		envRepository = new OciObjectStorageEnvironmentRepository(mockStorageClient, "mock-bucket", "mock-namespace",
				new String[0], mockServerProps);
		InputStream inputStream = new ByteArrayInputStream("foo:bar\n".getBytes());
		when(mockStorageClient.getObject(argThat(request -> request.getBucketName().equals("mock-bucket")
				&& request.getNamespaceName().equals("mock-namespace")
				&& request.getObjectName().equals("foo-bar.properties"))))
						.thenReturn(GetObjectResponse.builder().inputStream(inputStream).build());
		Environment env = envRepository.findOne("foo", "bar", "label");
		assertThat(env.getPropertySources().size()).isEqualTo(1);
		assertThat(env.getPropertySources().get(0).getSource().get("foo")).isEqualTo("bar");
	}

	@Test
	public void testSingleAppProfileYml() {
		envRepository = new OciObjectStorageEnvironmentRepository(mockStorageClient, "mock-bucket", "mock-namespace",
				new String[0], mockServerProps);
		InputStream inputStream = new ByteArrayInputStream("foo: bar\n".getBytes());
		when(mockStorageClient.getObject(argThat(request -> request.getBucketName().equals("mock-bucket")
				&& request.getNamespaceName().equals("mock-namespace")
				&& request.getObjectName().equals("foo-bar.yml"))))
						.thenReturn(GetObjectResponse.builder().inputStream(inputStream).build());
		Environment env = envRepository.findOne("foo", "bar", "label");
		assertThat(env.getPropertySources().size()).isEqualTo(1);
		assertThat(env.getPropertySources().get(0).getSource().get("foo")).isEqualTo("bar");
	}

	@Test
	public void testSingleAppProfileYaml() {
		envRepository = new OciObjectStorageEnvironmentRepository(mockStorageClient, "mock-bucket", "mock-namespace",
				new String[0], mockServerProps);
		InputStream inputStream = new ByteArrayInputStream("foo: bar\n".getBytes());
		when(mockStorageClient.getObject(argThat(request -> request.getBucketName().equals("mock-bucket")
				&& request.getNamespaceName().equals("mock-namespace")
				&& request.getObjectName().equals("foo-bar.yaml"))))
						.thenReturn(GetObjectResponse.builder().inputStream(inputStream).build());
		Environment env = envRepository.findOne("foo", "bar", "label");
		assertThat(env.getPropertySources().size()).isEqualTo(1);
		assertThat(env.getPropertySources().get(0).getSource().get("foo")).isEqualTo("bar");
	}

	@Test
	public void testSingleAppProfileJson() {
		envRepository = new OciObjectStorageEnvironmentRepository(mockStorageClient, "mock-bucket", "mock-namespace",
				new String[0], mockServerProps);
		InputStream inputStream = new ByteArrayInputStream("foo: bar\n".getBytes());
		when(mockStorageClient.getObject(argThat(request -> request.getBucketName().equals("mock-bucket")
				&& request.getNamespaceName().equals("mock-namespace")
				&& request.getObjectName().equals("foo-bar.json"))))
						.thenReturn(GetObjectResponse.builder().inputStream(inputStream).build());
		Environment env = envRepository.findOne("foo", "bar", "label");
		assertThat(env.getPropertySources().size()).isEqualTo(1);
		assertThat(env.getPropertySources().get(0).getSource().get("foo")).isEqualTo("bar");
	}

	// Tests for application-bar in all formats
	@Test
	public void testDefaultAppWithProfilePropsFile() {
		envRepository = new OciObjectStorageEnvironmentRepository(mockStorageClient, "mock-bucket", "mock-namespace",
				new String[0], mockServerProps);
		InputStream inputStream = new ByteArrayInputStream("foo:bar\n".getBytes());
		when(mockStorageClient.getObject(argThat(request -> request.getBucketName().equals("mock-bucket")
				&& request.getNamespaceName().equals("mock-namespace")
				&& request.getObjectName().equals("application-bar.properties"))))
						.thenReturn(GetObjectResponse.builder().inputStream(inputStream).build());
		Environment env = envRepository.findOne("foo", "bar", "label");
		assertThat(env.getPropertySources().size()).isEqualTo(1);
		assertThat(env.getPropertySources().get(0).getSource().get("foo")).isEqualTo("bar");
	}

	@Test
	public void testDefaultAppWithProfileYml() {
		envRepository = new OciObjectStorageEnvironmentRepository(mockStorageClient, "mock-bucket", "mock-namespace",
				new String[0], mockServerProps);
		InputStream inputStream = new ByteArrayInputStream("foo: bar\n".getBytes());
		when(mockStorageClient.getObject(argThat(request -> request.getBucketName().equals("mock-bucket")
				&& request.getNamespaceName().equals("mock-namespace")
				&& request.getObjectName().equals("application-bar.yml"))))
						.thenReturn(GetObjectResponse.builder().inputStream(inputStream).build());
		Environment env = envRepository.findOne("foo", "bar", "label");
		assertThat(env.getPropertySources().size()).isEqualTo(1);
		assertThat(env.getPropertySources().get(0).getSource().get("foo")).isEqualTo("bar");
	}

	@Test
	public void testDefaultAppWithProfileYaml() {
		envRepository = new OciObjectStorageEnvironmentRepository(mockStorageClient, "mock-bucket", "mock-namespace",
				new String[0], mockServerProps);
		InputStream inputStream = new ByteArrayInputStream("foo: bar\n".getBytes());
		when(mockStorageClient.getObject(argThat(request -> request.getBucketName().equals("mock-bucket")
				&& request.getNamespaceName().equals("mock-namespace")
				&& request.getObjectName().equals("application-bar.yaml"))))
						.thenReturn(GetObjectResponse.builder().inputStream(inputStream).build());
		Environment env = envRepository.findOne("foo", "bar", "label");
		assertThat(env.getPropertySources().size()).isEqualTo(1);
		assertThat(env.getPropertySources().get(0).getSource().get("foo")).isEqualTo("bar");
	}

	@Test
	public void testDefaultAppWithProfileJson() {
		envRepository = new OciObjectStorageEnvironmentRepository(mockStorageClient, "mock-bucket", "mock-namespace",
				new String[0], mockServerProps);
		InputStream inputStream = new ByteArrayInputStream("foo: bar\n".getBytes());
		when(mockStorageClient.getObject(argThat(request -> request.getBucketName().equals("mock-bucket")
				&& request.getNamespaceName().equals("mock-namespace")
				&& request.getObjectName().equals("application-bar.json"))))
						.thenReturn(GetObjectResponse.builder().inputStream(inputStream).build());
		Environment env = envRepository.findOne("foo", "bar", "label");
		assertThat(env.getPropertySources().size()).isEqualTo(1);
		assertThat(env.getPropertySources().get(0).getSource().get("foo")).isEqualTo("bar");
	}

	// Test for application name with no profile in all formats
	@Test
	public void testAppWithoutProfilePropsFile() {
		envRepository = new OciObjectStorageEnvironmentRepository(mockStorageClient, "mock-bucket", "mock-namespace",
				new String[0], mockServerProps);
		InputStream inputStream = new ByteArrayInputStream("foo:bar\n".getBytes());
		when(mockStorageClient.getObject(argThat(request -> request.getBucketName().equals("mock-bucket")
				&& request.getNamespaceName().equals("mock-namespace")
				&& request.getObjectName().equals("foo.properties"))))
						.thenReturn(GetObjectResponse.builder().inputStream(inputStream).build());
		Environment env = envRepository.findOne("foo", "bar", "label");
		assertThat(env.getPropertySources().size()).isEqualTo(1);
		assertThat(env.getPropertySources().get(0).getSource().get("foo")).isEqualTo("bar");
	}

	@Test
	public void testAppWithoutProfileYml() {
		envRepository = new OciObjectStorageEnvironmentRepository(mockStorageClient, "mock-bucket", "mock-namespace",
				new String[0], mockServerProps);
		InputStream inputStream = new ByteArrayInputStream("foo: bar\n".getBytes());
		when(mockStorageClient.getObject(argThat(request -> request.getBucketName().equals("mock-bucket")
				&& request.getNamespaceName().equals("mock-namespace") && request.getObjectName().equals("foo.yml"))))
						.thenReturn(GetObjectResponse.builder().inputStream(inputStream).build());
		Environment env = envRepository.findOne("foo", "bar", "label");
		assertThat(env.getPropertySources().size()).isEqualTo(1);
		assertThat(env.getPropertySources().get(0).getSource().get("foo")).isEqualTo("bar");
	}

	@Test
	public void testAppWithoutProfileYaml() {
		envRepository = new OciObjectStorageEnvironmentRepository(mockStorageClient, "mock-bucket", "mock-namespace",
				new String[0], mockServerProps);
		InputStream inputStream = new ByteArrayInputStream("foo: bar\n".getBytes());
		when(mockStorageClient.getObject(argThat(request -> request.getBucketName().equals("mock-bucket")
				&& request.getNamespaceName().equals("mock-namespace") && request.getObjectName().equals("foo.yaml"))))
						.thenReturn(GetObjectResponse.builder().inputStream(inputStream).build());
		Environment env = envRepository.findOne("foo", "bar", "label");
		assertThat(env.getPropertySources().size()).isEqualTo(1);
		assertThat(env.getPropertySources().get(0).getSource().get("foo")).isEqualTo("bar");
	}

	@Test
	public void testAppWithoutProfileJson() {
		envRepository = new OciObjectStorageEnvironmentRepository(mockStorageClient, "mock-bucket", "mock-namespace",
				new String[0], mockServerProps);
		InputStream inputStream = new ByteArrayInputStream("foo: bar\n".getBytes());
		when(mockStorageClient.getObject(argThat(request -> request.getBucketName().equals("mock-bucket")
				&& request.getNamespaceName().equals("mock-namespace") && request.getObjectName().equals("foo.json"))))
						.thenReturn(GetObjectResponse.builder().inputStream(inputStream).build());
		Environment env = envRepository.findOne("foo", "bar", "label");
		assertThat(env.getPropertySources().size()).isEqualTo(1);
		assertThat(env.getPropertySources().get(0).getSource().get("foo")).isEqualTo("bar");
	}

	// Test for default application with no profile in all formats
	@Test
	public void testDefaultAppWithoutProfilePropsFile() {
		envRepository = new OciObjectStorageEnvironmentRepository(mockStorageClient, "mock-bucket", "mock-namespace",
				new String[0], mockServerProps);
		InputStream inputStream = new ByteArrayInputStream("foo:bar\n".getBytes());
		when(mockStorageClient.getObject(argThat(request -> request.getBucketName().equals("mock-bucket")
				&& request.getNamespaceName().equals("mock-namespace")
				&& request.getObjectName().equals("application.properties"))))
						.thenReturn(GetObjectResponse.builder().inputStream(inputStream).build());
		Environment env = envRepository.findOne("foo", "bar", "label");
		assertThat(env.getPropertySources().size()).isEqualTo(1);
		assertThat(env.getPropertySources().get(0).getSource().get("foo")).isEqualTo("bar");
	}

	@Test
	public void testDefaultAppWithoutProfileYml() {
		envRepository = new OciObjectStorageEnvironmentRepository(mockStorageClient, "mock-bucket", "mock-namespace",
				new String[0], mockServerProps);
		InputStream inputStream = new ByteArrayInputStream("foo: bar\n".getBytes());
		when(mockStorageClient.getObject(argThat(request -> request.getBucketName().equals("mock-bucket")
				&& request.getNamespaceName().equals("mock-namespace")
				&& request.getObjectName().equals("application.yml"))))
						.thenReturn(GetObjectResponse.builder().inputStream(inputStream).build());
		Environment env = envRepository.findOne("foo", "bar", "label");
		assertThat(env.getPropertySources().size()).isEqualTo(1);
		assertThat(env.getPropertySources().get(0).getSource().get("foo")).isEqualTo("bar");
	}

	@Test
	public void testDefaultAppWithoutProfileYaml() {
		envRepository = new OciObjectStorageEnvironmentRepository(mockStorageClient, "mock-bucket", "mock-namespace",
				new String[0], mockServerProps);
		InputStream inputStream = new ByteArrayInputStream("foo: bar\n".getBytes());
		when(mockStorageClient.getObject(argThat(request -> request.getBucketName().equals("mock-bucket")
				&& request.getNamespaceName().equals("mock-namespace")
				&& request.getObjectName().equals("application.yaml"))))
						.thenReturn(GetObjectResponse.builder().inputStream(inputStream).build());
		Environment env = envRepository.findOne("foo", "bar", "label");
		assertThat(env.getPropertySources().size()).isEqualTo(1);
		assertThat(env.getPropertySources().get(0).getSource().get("foo")).isEqualTo("bar");
	}

	@Test
	public void testDefaultAppWithoutProfileJson() {
		envRepository = new OciObjectStorageEnvironmentRepository(mockStorageClient, "mock-bucket", "mock-namespace",
				new String[0], mockServerProps);
		InputStream inputStream = new ByteArrayInputStream("foo: bar\n".getBytes());
		when(mockStorageClient.getObject(argThat(request -> request.getBucketName().equals("mock-bucket")
				&& request.getNamespaceName().equals("mock-namespace")
				&& request.getObjectName().equals("application.json"))))
						.thenReturn(GetObjectResponse.builder().inputStream(inputStream).build());
		Environment env = envRepository.findOne("foo", "bar", "label");
		assertThat(env.getPropertySources().size()).isEqualTo(1);
		assertThat(env.getPropertySources().get(0).getSource().get("foo")).isEqualTo("bar");
	}

	// Test for foo-bar.properties in various search-paths
	@Test
	public void testApplicationPlaceholderSearchPathForAppDashProfile() {
		envRepository = new OciObjectStorageEnvironmentRepository(mockStorageClient, "mock-bucket", "mock-namespace",
				new String[] { "{application}" }, mockServerProps);
		InputStream inputStream = new ByteArrayInputStream("foo:bar\n".getBytes());
		when(mockStorageClient.getObject(argThat(request -> request.getBucketName().equals("mock-bucket")
				&& request.getNamespaceName().equals("mock-namespace")
				&& request.getObjectName().equals("foo/foo-bar.properties"))))
						.thenReturn(GetObjectResponse.builder().inputStream(inputStream).build());
		Environment env = envRepository.findOne("foo", "bar", "label");
		assertThat(env.getPropertySources().size()).isEqualTo(1);
		assertThat(env.getPropertySources().get(0).getSource().get("foo")).isEqualTo("bar");
	}

	@Test
	public void testProfilePlaceholderSearchPathForAppDashProfile() {
		envRepository = new OciObjectStorageEnvironmentRepository(mockStorageClient, "mock-bucket", "mock-namespace",
				new String[] { "{profile}" }, mockServerProps);
		InputStream inputStream = new ByteArrayInputStream("foo:bar\n".getBytes());
		when(mockStorageClient.getObject(argThat(request -> request.getBucketName().equals("mock-bucket")
				&& request.getNamespaceName().equals("mock-namespace")
				&& request.getObjectName().equals("bar/foo-bar.properties"))))
						.thenReturn(GetObjectResponse.builder().inputStream(inputStream).build());
		Environment env = envRepository.findOne("foo", "bar", "label");
		assertThat(env.getPropertySources().size()).isEqualTo(1);
		assertThat(env.getPropertySources().get(0).getSource().get("foo")).isEqualTo("bar");
	}

	@Test
	public void testLabelPlaceholderSearchPathForAppDashProfile() {
		envRepository = new OciObjectStorageEnvironmentRepository(mockStorageClient, "mock-bucket", "mock-namespace",
				new String[] { "{label}" }, mockServerProps);
		InputStream inputStream = new ByteArrayInputStream("foo:bar\n".getBytes());
		when(mockStorageClient.getObject(argThat(request -> request.getBucketName().equals("mock-bucket")
				&& request.getNamespaceName().equals("mock-namespace")
				&& request.getObjectName().equals("my-label/foo-bar.properties"))))
						.thenReturn(GetObjectResponse.builder().inputStream(inputStream).build());
		Environment env = envRepository.findOne("foo", "bar", "my-label");
		assertThat(env.getPropertySources().size()).isEqualTo(1);
		assertThat(env.getPropertySources().get(0).getSource().get("foo")).isEqualTo("bar");
	}

	@Test
	public void testNoPlaceholderSearchPathForAppDashProfile() {
		envRepository = new OciObjectStorageEnvironmentRepository(mockStorageClient, "mock-bucket", "mock-namespace",
				new String[] { "folder" }, mockServerProps);
		InputStream inputStream = new ByteArrayInputStream("foo:bar\n".getBytes());
		when(mockStorageClient.getObject(argThat(request -> request.getBucketName().equals("mock-bucket")
				&& request.getNamespaceName().equals("mock-namespace")
				&& request.getObjectName().equals("folder/foo-bar.properties"))))
						.thenReturn(GetObjectResponse.builder().inputStream(inputStream).build());
		Environment env = envRepository.findOne("foo", "bar", "my-label");
		assertThat(env.getPropertySources().size()).isEqualTo(1);
		assertThat(env.getPropertySources().get(0).getSource().get("foo")).isEqualTo("bar");
	}

	// Tests for application-bar.properties in various search paths
	@Test
	public void testApplicationPlaceholderSearchPathForDefaultApplicationDashProfile() {
		envRepository = new OciObjectStorageEnvironmentRepository(mockStorageClient, "mock-bucket", "mock-namespace",
				new String[] { "{application}" }, mockServerProps);
		InputStream inputStream = new ByteArrayInputStream("foo:bar\n".getBytes());
		when(mockStorageClient.getObject(argThat(request -> request.getBucketName().equals("mock-bucket")
				&& request.getNamespaceName().equals("mock-namespace")
				&& request.getObjectName().equals("foo/application-bar.properties"))))
						.thenReturn(GetObjectResponse.builder().inputStream(inputStream).build());
		Environment env = envRepository.findOne("foo", "bar", "label");
		assertThat(env.getPropertySources().size()).isEqualTo(1);
		assertThat(env.getPropertySources().get(0).getSource().get("foo")).isEqualTo("bar");
	}

	@Test
	public void testProfilePlaceholderSearchPathForDefaultApplicationDashProfile() {
		envRepository = new OciObjectStorageEnvironmentRepository(mockStorageClient, "mock-bucket", "mock-namespace",
				new String[] { "{profile}" }, mockServerProps);
		InputStream inputStream = new ByteArrayInputStream("foo:bar\n".getBytes());
		when(mockStorageClient.getObject(argThat(request -> request.getBucketName().equals("mock-bucket")
				&& request.getNamespaceName().equals("mock-namespace")
				&& request.getObjectName().equals("bar/application-bar.properties"))))
						.thenReturn(GetObjectResponse.builder().inputStream(inputStream).build());
		Environment env = envRepository.findOne("foo", "bar", "label");
		assertThat(env.getPropertySources().size()).isEqualTo(1);
		assertThat(env.getPropertySources().get(0).getSource().get("foo")).isEqualTo("bar");
	}

	@Test
	public void testLabelPlaceholderSearchPathForDefaultApplicationDashProfile() {
		envRepository = new OciObjectStorageEnvironmentRepository(mockStorageClient, "mock-bucket", "mock-namespace",
				new String[] { "{label}" }, mockServerProps);
		InputStream inputStream = new ByteArrayInputStream("foo:bar\n".getBytes());
		when(mockStorageClient.getObject(argThat(request -> request.getBucketName().equals("mock-bucket")
				&& request.getNamespaceName().equals("mock-namespace")
				&& request.getObjectName().equals("my-label/application-bar.properties"))))
						.thenReturn(GetObjectResponse.builder().inputStream(inputStream).build());
		Environment env = envRepository.findOne("foo", "bar", "my-label");
		assertThat(env.getPropertySources().size()).isEqualTo(1);
		assertThat(env.getPropertySources().get(0).getSource().get("foo")).isEqualTo("bar");
	}

	@Test
	public void testNoPlaceholderSearchPathForDefaultApplicationDashProfile() {
		envRepository = new OciObjectStorageEnvironmentRepository(mockStorageClient, "mock-bucket", "mock-namespace",
				new String[] { "folder" }, mockServerProps);
		InputStream inputStream = new ByteArrayInputStream("foo:bar\n".getBytes());
		when(mockStorageClient.getObject(argThat(request -> request.getBucketName().equals("mock-bucket")
				&& request.getNamespaceName().equals("mock-namespace")
				&& request.getObjectName().equals("folder/application-bar.properties"))))
						.thenReturn(GetObjectResponse.builder().inputStream(inputStream).build());
		Environment env = envRepository.findOne("foo", "bar", "my-label");
		assertThat(env.getPropertySources().size()).isEqualTo(1);
		assertThat(env.getPropertySources().get(0).getSource().get("foo")).isEqualTo("bar");
	}

	// Tests for foo.properties in various search paths
	@Test
	public void testApplicationPlaceholderSearchPath_ForAppNoProfile() {
		envRepository = new OciObjectStorageEnvironmentRepository(mockStorageClient, "mock-bucket", "mock-namespace",
				new String[] { "{application}" }, mockServerProps);
		InputStream inputStream = new ByteArrayInputStream("foo:bar\n".getBytes());
		when(mockStorageClient.getObject(argThat(request -> request.getBucketName().equals("mock-bucket")
				&& request.getNamespaceName().equals("mock-namespace")
				&& request.getObjectName().equals("foo/foo.properties"))))
						.thenReturn(GetObjectResponse.builder().inputStream(inputStream).build());
		Environment env = envRepository.findOne("foo", "bar", "label");
		assertThat(env.getPropertySources().size()).isEqualTo(1);
		assertThat(env.getPropertySources().get(0).getSource().get("foo")).isEqualTo("bar");
	}

	@Test
	public void testProfilePlaceholderSearchPath_ForAppNoProfile() {
		envRepository = new OciObjectStorageEnvironmentRepository(mockStorageClient, "mock-bucket", "mock-namespace",
				new String[] { "{profile}" }, mockServerProps);
		InputStream inputStream = new ByteArrayInputStream("foo:bar\n".getBytes());
		when(mockStorageClient.getObject(argThat(request -> request.getBucketName().equals("mock-bucket")
				&& request.getNamespaceName().equals("mock-namespace")
				&& request.getObjectName().equals("bar/foo.properties"))))
						.thenReturn(GetObjectResponse.builder().inputStream(inputStream).build());
		Environment env = envRepository.findOne("foo", "bar", "label");
		assertThat(env.getPropertySources().size()).isEqualTo(1);
		assertThat(env.getPropertySources().get(0).getSource().get("foo")).isEqualTo("bar");
	}

	@Test
	public void testLabelPlaceholderSearchPath_ForAppNoProfile() {
		envRepository = new OciObjectStorageEnvironmentRepository(mockStorageClient, "mock-bucket", "mock-namespace",
				new String[] { "{label}" }, mockServerProps);
		InputStream inputStream = new ByteArrayInputStream("foo:bar\n".getBytes());
		when(mockStorageClient.getObject(argThat(request -> request.getBucketName().equals("mock-bucket")
				&& request.getNamespaceName().equals("mock-namespace")
				&& request.getObjectName().equals("my-label/foo.properties"))))
						.thenReturn(GetObjectResponse.builder().inputStream(inputStream).build());
		Environment env = envRepository.findOne("foo", "bar", "my-label");
		assertThat(env.getPropertySources().size()).isEqualTo(1);
		assertThat(env.getPropertySources().get(0).getSource().get("foo")).isEqualTo("bar");
	}

	@Test
	public void testNoPlaceholderSearchPath_ForAppNoProfile() {
		envRepository = new OciObjectStorageEnvironmentRepository(mockStorageClient, "mock-bucket", "mock-namespace",
				new String[] { "folder" }, mockServerProps);
		InputStream inputStream = new ByteArrayInputStream("foo:bar\n".getBytes());
		when(mockStorageClient.getObject(argThat(request -> request.getBucketName().equals("mock-bucket")
				&& request.getNamespaceName().equals("mock-namespace")
				&& request.getObjectName().equals("folder/foo.properties"))))
						.thenReturn(GetObjectResponse.builder().inputStream(inputStream).build());
		Environment env = envRepository.findOne("foo", "bar", "my-label");
		assertThat(env.getPropertySources().size()).isEqualTo(1);
		assertThat(env.getPropertySources().get(0).getSource().get("foo")).isEqualTo("bar");
	}

	// Tests for application.properties in various searchpaths
	@Test
	public void testApplicationPlaceholderSearchPath_ForDefaultAppNoProfile() {
		envRepository = new OciObjectStorageEnvironmentRepository(mockStorageClient, "mock-bucket", "mock-namespace",
				new String[] { "{application}" }, mockServerProps);
		InputStream inputStream = new ByteArrayInputStream("foo:bar\n".getBytes());
		when(mockStorageClient.getObject(argThat(request -> request.getBucketName().equals("mock-bucket")
				&& request.getNamespaceName().equals("mock-namespace")
				&& request.getObjectName().equals("foo/application.properties"))))
						.thenReturn(GetObjectResponse.builder().inputStream(inputStream).build());
		Environment env = envRepository.findOne("foo", "bar", "label");
		assertThat(env.getPropertySources().size()).isEqualTo(1);
		assertThat(env.getPropertySources().get(0).getSource().get("foo")).isEqualTo("bar");
	}

	@Test
	public void testProfilePlaceholderSearchPath_ForDefaultAppNoProfile() {
		envRepository = new OciObjectStorageEnvironmentRepository(mockStorageClient, "mock-bucket", "mock-namespace",
				new String[] { "{profile}" }, mockServerProps);
		InputStream inputStream = new ByteArrayInputStream("foo:bar\n".getBytes());
		when(mockStorageClient.getObject(argThat(request -> request.getBucketName().equals("mock-bucket")
				&& request.getNamespaceName().equals("mock-namespace")
				&& request.getObjectName().equals("bar/application.properties"))))
						.thenReturn(GetObjectResponse.builder().inputStream(inputStream).build());
		Environment env = envRepository.findOne("foo", "bar", "label");
		assertThat(env.getPropertySources().size()).isEqualTo(1);
		assertThat(env.getPropertySources().get(0).getSource().get("foo")).isEqualTo("bar");
	}

	@Test
	public void testLabelPlaceholderSearchPath_ForDefaultAppNoProfile() {
		envRepository = new OciObjectStorageEnvironmentRepository(mockStorageClient, "mock-bucket", "mock-namespace",
				new String[] { "{label}" }, mockServerProps);
		InputStream inputStream = new ByteArrayInputStream("foo:bar\n".getBytes());
		when(mockStorageClient.getObject(argThat(request -> request.getBucketName().equals("mock-bucket")
				&& request.getNamespaceName().equals("mock-namespace")
				&& request.getObjectName().equals("my-label/application.properties"))))
						.thenReturn(GetObjectResponse.builder().inputStream(inputStream).build());
		Environment env = envRepository.findOne("foo", "bar", "my-label");
		assertThat(env.getPropertySources().size()).isEqualTo(1);
		assertThat(env.getPropertySources().get(0).getSource().get("foo")).isEqualTo("bar");
	}

	@Test
	public void testNoPlaceholderSearchPath_ForDefaultAppNoProfile() {
		envRepository = new OciObjectStorageEnvironmentRepository(mockStorageClient, "mock-bucket", "mock-namespace",
				new String[] { "folder" }, mockServerProps);
		InputStream inputStream = new ByteArrayInputStream("foo:bar\n".getBytes());
		when(mockStorageClient.getObject(argThat(request -> request.getBucketName().equals("mock-bucket")
				&& request.getNamespaceName().equals("mock-namespace")
				&& request.getObjectName().equals("folder/application.properties"))))
						.thenReturn(GetObjectResponse.builder().inputStream(inputStream).build());
		Environment env = envRepository.findOne("foo", "bar", "my-label");
		assertThat(env.getPropertySources().size()).isEqualTo(1);
		assertThat(env.getPropertySources().get(0).getSource().get("foo")).isEqualTo("bar");
	}

	// Verify the order of the returned properties with no search path
	@Test
	public void testCorrectPropertyOverrideOrderWithNosearchPath() {
		envRepository = new OciObjectStorageEnvironmentRepository(mockStorageClient, "mock-bucket", "mock-namespace",
				new String[] {}, mockServerProps);
		InputStream inputStream = new ByteArrayInputStream("foo: bar\n".getBytes());
		InputStream inputStream2 = new ByteArrayInputStream("foo: bar2\n".getBytes());
		InputStream inputStream3 = new ByteArrayInputStream("foo: bar3\n".getBytes());
		InputStream inputStream4 = new ByteArrayInputStream("foo: bar4\n".getBytes());
		doReturn(GetObjectResponse.builder().inputStream(inputStream).build()).when(mockStorageClient)
				.getObject(argThat(new OciRequestMatcher("mock-namespace", "mock-bucket", "application.properties")));
		doReturn(GetObjectResponse.builder().inputStream(inputStream2).build()).when(mockStorageClient)
				.getObject(argThat(new OciRequestMatcher("mock-namespace", "mock-bucket", "foo.properties")));
		doReturn(GetObjectResponse.builder().inputStream(inputStream3).build()).when(mockStorageClient).getObject(
				argThat(new OciRequestMatcher("mock-namespace", "mock-bucket", "application-bar.properties")));
		doReturn(GetObjectResponse.builder().inputStream(inputStream4).build()).when(mockStorageClient)
				.getObject(argThat(new OciRequestMatcher("mock-namespace", "mock-bucket", "foo-bar.properties")));
		Environment env = envRepository.findOne("foo", "bar", "my-label");
		assertThat(env.getPropertySources().size()).isEqualTo(4);
		assertThat(env.getPropertySources().get(0).getSource().get("foo")).isEqualTo("bar4");
		assertThat(env.getPropertySources().get(1).getSource().get("foo")).isEqualTo("bar3");
		assertThat(env.getPropertySources().get(2).getSource().get("foo")).isEqualTo("bar2");
		assertThat(env.getPropertySources().get(3).getSource().get("foo")).isEqualTo("bar");
	}

	// Verify the order of the returned properties with search path
	@Test
	public void testCorrectPropertyOverrideOrderWithSearchPath() {
		envRepository = new OciObjectStorageEnvironmentRepository(mockStorageClient, "mock-bucket", "mock-namespace",
				new String[] { "{application}" }, mockServerProps);
		InputStream inputStream = new ByteArrayInputStream("foo: bar\n".getBytes());
		InputStream inputStream2 = new ByteArrayInputStream("foo: bar2\n".getBytes());
		InputStream inputStream3 = new ByteArrayInputStream("foo: bar3\n".getBytes());
		InputStream inputStream4 = new ByteArrayInputStream("foo: bar4\n".getBytes());
		InputStream inputStream5 = new ByteArrayInputStream("foo: bar5\n".getBytes());
		InputStream inputStream6 = new ByteArrayInputStream("foo: bar6\n".getBytes());
		InputStream inputStream7 = new ByteArrayInputStream("foo: bar7\n".getBytes());
		InputStream inputStream8 = new ByteArrayInputStream("foo: bar8\n".getBytes());
		doReturn(GetObjectResponse.builder().inputStream(inputStream).build()).when(mockStorageClient)
				.getObject(argThat(new OciRequestMatcher("mock-namespace", "mock-bucket", "application.properties")));
		doReturn(GetObjectResponse.builder().inputStream(inputStream2).build()).when(mockStorageClient)
				.getObject(argThat(new OciRequestMatcher("mock-namespace", "mock-bucket", "foo.properties")));
		doReturn(GetObjectResponse.builder().inputStream(inputStream3).build()).when(mockStorageClient).getObject(
				argThat(new OciRequestMatcher("mock-namespace", "mock-bucket", "application-bar.properties")));
		doReturn(GetObjectResponse.builder().inputStream(inputStream4).build()).when(mockStorageClient)
				.getObject(argThat(new OciRequestMatcher("mock-namespace", "mock-bucket", "foo-bar.properties")));
		doReturn(GetObjectResponse.builder().inputStream(inputStream8).build()).when(mockStorageClient).getObject(
				argThat(new OciRequestMatcher("mock-namespace", "mock-bucket", "foo/application.properties")));
		doReturn(GetObjectResponse.builder().inputStream(inputStream7).build()).when(mockStorageClient)
				.getObject(argThat(new OciRequestMatcher("mock-namespace", "mock-bucket", "foo/foo.properties")));
		doReturn(GetObjectResponse.builder().inputStream(inputStream6).build()).when(mockStorageClient).getObject(
				argThat(new OciRequestMatcher("mock-namespace", "mock-bucket", "foo/application-bar.properties")));
		doReturn(GetObjectResponse.builder().inputStream(inputStream5).build()).when(mockStorageClient)
				.getObject(argThat(new OciRequestMatcher("mock-namespace", "mock-bucket", "foo/foo-bar.properties")));
		Environment env = envRepository.findOne("foo", "bar", "my-label");
		assertThat(env.getPropertySources().size()).isEqualTo(8);
		assertThat(env.getPropertySources().get(0).getSource().get("foo")).isEqualTo("bar5");
		assertThat(env.getPropertySources().get(1).getSource().get("foo")).isEqualTo("bar6");
		assertThat(env.getPropertySources().get(2).getSource().get("foo")).isEqualTo("bar7");
		assertThat(env.getPropertySources().get(3).getSource().get("foo")).isEqualTo("bar8");
		assertThat(env.getPropertySources().get(4).getSource().get("foo")).isEqualTo("bar4");
		assertThat(env.getPropertySources().get(5).getSource().get("foo")).isEqualTo("bar3");
		assertThat(env.getPropertySources().get(6).getSource().get("foo")).isEqualTo("bar2");
		assertThat(env.getPropertySources().get(7).getSource().get("foo")).isEqualTo("bar");
	}

	class OciRequestMatcher implements ArgumentMatcher<GetObjectRequest> {

		private String namespaceName;

		private String bucketName;

		private String objectName;

		OciRequestMatcher(String namespaceName, String bucketName, String objectName) {
			this.namespaceName = namespaceName;
			this.bucketName = bucketName;
			this.objectName = objectName;
		}

		@Override
		public boolean matches(GetObjectRequest argument) {
			System.out.println(argument);
			return argument.getBucketName().equals(this.bucketName)
					&& argument.getNamespaceName().equals(this.namespaceName)
					&& argument.getObjectName().equals(this.objectName);
		}

	}

}
