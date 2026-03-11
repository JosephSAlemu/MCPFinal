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

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

class PetTests {

	@Test
	void setBirthDate_Valid_birthDate_valid_instance() {
		Pet pet = new Pet();
		LocalDate date = LocalDate.of(2022, 6, 1);
		pet.setBirthDate(date);
		assertThat(pet.getBirthDate()).isEqualTo(date);
	}

	@Test
	void setBirthDate_Invalid_birthDate_null() {
		Pet pet = new Pet();
		pet.setBirthDate(null);
		assertThat(pet.getBirthDate()).isNull();
	}

	@Test
	void setType_Valid_type_valid_instance() {
		Pet pet = new Pet();
		PetType type = new PetType();
		type.setName("Cat");
		pet.setType(type);
		assertThat(pet.getType()).isEqualTo(type);
	}

	@Test
	void setType_Invalid_type_null() {
		Pet pet = new Pet();
		pet.setType(null);
		assertThat(pet.getType()).isNull();
	}

	@Test
	void getVisits_returnsEmptyCollectionInitially() {
		Pet pet = new Pet();
		assertThat(pet.getVisits()).isEmpty();
	}

	@Test
	void addVisit_Valid_visit_valid_instance() {
		Pet pet = new Pet();
		Visit visit = new Visit();
		visit.setDescription("Checkup");
		pet.addVisit(visit);
		assertThat(pet.getVisits()).hasSize(1).contains(visit);
	}

}
