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
package org.springframework.samples.petclinic;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Tests for {@link PetClinicApplication}.
 */
@SpringBootTest
class PetClinicApplicationTests {

	@Test
	void contextLoads() {
		// Verifies the Spring application context loads successfully
	}

	@Test
	void mainMethodCallsSpringApplicationRun() {
		try (MockedStatic<SpringApplication> mocked = Mockito.mockStatic(SpringApplication.class)) {
			PetClinicApplication.main(new String[] {});
			mocked.verify(() -> SpringApplication.run(PetClinicApplication.class, new String[] {}));
		}
	}

	// --- Equivalence Partitioning Tests ---

	/** EC: valid args - typical non-empty args array */
	@Test
	void testMain_Valid_args_typical() {
		try (MockedStatic<SpringApplication> mocked = Mockito.mockStatic(SpringApplication.class)) {
			String[] args = { "hello" };
			PetClinicApplication.main(args);
			mocked.verify(() -> SpringApplication.run(PetClinicApplication.class, args));
		}
	}

	/** EC: valid args - single character argument */
	@Test
	void testMain_Valid_args_single_char() {
		try (MockedStatic<SpringApplication> mocked = Mockito.mockStatic(SpringApplication.class)) {
			String[] args = { "a" };
			PetClinicApplication.main(args);
			mocked.verify(() -> SpringApplication.run(PetClinicApplication.class, args));
		}
	}

	/** EC: valid args - args containing spaces */
	@Test
	void testMain_Valid_args_with_spaces() {
		try (MockedStatic<SpringApplication> mocked = Mockito.mockStatic(SpringApplication.class)) {
			String[] args = { "hello world" };
			PetClinicApplication.main(args);
			mocked.verify(() -> SpringApplication.run(PetClinicApplication.class, args));
		}
	}

	/** EC: valid args - empty string array (no args passed) */
	@Test
	void testMain_Invalid_args_empty() {
		try (MockedStatic<SpringApplication> mocked = Mockito.mockStatic(SpringApplication.class)) {
			String[] args = new String[0];
			PetClinicApplication.main(args);
			mocked.verify(() -> SpringApplication.run(PetClinicApplication.class, args));
		}
	}

}
