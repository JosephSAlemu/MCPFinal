/*
 * Copyright 2012-2025 the original author or authors.
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
package org.springframework.samples.petclinic.system;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Locale;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

class WebConfigurationTests {

	@Test
	void localeResolver_returnsSessionLocaleResolver() {
		WebConfiguration config = new WebConfiguration();
		LocaleResolver resolver = config.localeResolver();
		assertThat(resolver).isInstanceOf(SessionLocaleResolver.class);
	}

	@Test
	void localeResolver_defaultLocaleIsEnglish() {
		WebConfiguration config = new WebConfiguration();
		SessionLocaleResolver resolver = (SessionLocaleResolver) config.localeResolver();
		// Resolving with a request that has no session locale should return the default
		assertThat(resolver.resolveLocale(new MockHttpServletRequest())).isEqualTo(Locale.ENGLISH);
	}

	@Test
	void localeChangeInterceptor_returnsInterceptorWithLangParam() {
		WebConfiguration config = new WebConfiguration();
		LocaleChangeInterceptor interceptor = config.localeChangeInterceptor();
		assertThat(interceptor.getParamName()).isEqualTo("lang");
	}

	@Test
	void addInterceptors_registersWithoutError() {
		WebConfiguration config = new WebConfiguration();
		InterceptorRegistry registry = new InterceptorRegistry();
		// EP: valid registry — should complete without exception
		config.addInterceptors(registry);
	}

	// EP Tests based on MCP tool

	@Test
	void testAddInterceptors_Valid_registry_valid_instance() {
		WebConfiguration config = new WebConfiguration();
		InterceptorRegistry registry = new InterceptorRegistry();
		config.addInterceptors(registry);
		// Method executes successfully with a valid registry
	}

	@Test
	void testAddInterceptors_Invalid_registry_null() {
		WebConfiguration config = new WebConfiguration();
		// Passing null registry should throw NullPointerException
		assertThrows(NullPointerException.class, () -> config.addInterceptors(null));
	}

}
