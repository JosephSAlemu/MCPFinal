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
package org.springframework.samples.petclinic.vet;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class SpecialtyTests {

	@Test
	void setGetName_storesAndReturnsName() {
		Specialty specialty = new Specialty();
		specialty.setName("Dentistry");
		assertThat(specialty.getName()).isEqualTo("Dentistry");
	}

	@Test
	void isNew_withNullId_returnsTrue() {
		Specialty specialty = new Specialty();
		assertThat(specialty.isNew()).isTrue();
	}

	@Test
	void isNew_withIdSet_returnsFalse() {
		Specialty specialty = new Specialty();
		specialty.setId(1);
		assertThat(specialty.isNew()).isFalse();
	}

	@Test
	void toString_returnsName() {
		Specialty specialty = new Specialty();
		specialty.setName("Radiology");
		assertThat(specialty.toString()).isEqualTo("Radiology");
	}

	@Test
	void toString_withNullName_returnsNullPlaceholder() {
		Specialty specialty = new Specialty();
		assertThat(specialty.toString()).isEqualTo("<null>");
	}

}
