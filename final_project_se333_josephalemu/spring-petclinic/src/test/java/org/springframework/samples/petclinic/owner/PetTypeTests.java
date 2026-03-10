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
package org.springframework.samples.petclinic.owner;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class PetTypeTests {

	@Test
	void setGetName_storesAndReturnsName() {
		PetType petType = new PetType();
		petType.setName("Cat");
		assertThat(petType.getName()).isEqualTo("Cat");
	}

	@Test
	void isNew_withNullId_returnsTrue() {
		PetType petType = new PetType();
		assertThat(petType.isNew()).isTrue();
	}

	@Test
	void isNew_withIdSet_returnsFalse() {
		PetType petType = new PetType();
		petType.setId(1);
		assertThat(petType.isNew()).isFalse();
	}

	@Test
	void toString_returnsName() {
		PetType petType = new PetType();
		petType.setName("Dog");
		assertThat(petType.toString()).isEqualTo("Dog");
	}

	@Test
	void toString_withNullName_returnsNullPlaceholder() {
		PetType petType = new PetType();
		assertThat(petType.toString()).isEqualTo("<null>");
	}

}
