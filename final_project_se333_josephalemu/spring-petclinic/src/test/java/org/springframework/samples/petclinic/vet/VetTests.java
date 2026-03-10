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

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

class VetTests {

	@Test
	void getSpecialtiesInternal_initializesWhenNull() {
		Vet vet = new Vet();
		Set<Specialty> specialties = vet.getSpecialtiesInternal();
		assertThat(specialties).isNotNull().isEmpty();
	}

	@Test
	void getSpecialtiesInternal_returnsSameSetOnSubsequentCalls() {
		Vet vet = new Vet();
		Set<Specialty> first = vet.getSpecialtiesInternal();
		Set<Specialty> second = vet.getSpecialtiesInternal();
		assertThat(first).isSameAs(second);
	}

	@Test
	void getNrOfSpecialties_returnsZeroInitially() {
		Vet vet = new Vet();
		assertThat(vet.getNrOfSpecialties()).isZero();
	}

	@Test
	void addSpecialty_incrementsCount() {
		Vet vet = new Vet();
		Specialty specialty = new Specialty();
		specialty.setName("Dentistry");
		vet.addSpecialty(specialty);
		assertThat(vet.getNrOfSpecialties()).isEqualTo(1);
	}

	@Test
	void getSpecialties_returnsSortedByName() {
		Vet vet = new Vet();
		Specialty s1 = new Specialty();
		s1.setName("Radiology");
		Specialty s2 = new Specialty();
		s2.setName("Acupuncture");
		Specialty s3 = new Specialty();
		s3.setName("Dentistry");
		vet.addSpecialty(s1);
		vet.addSpecialty(s2);
		vet.addSpecialty(s3);
		List<Specialty> result = vet.getSpecialties();
		assertThat(result).extracting(Specialty::getName).containsExactly("Acupuncture", "Dentistry", "Radiology");
	}

	@Test
	void getSpecialties_emptyListWhenNoSpecialties() {
		Vet vet = new Vet();
		assertThat(vet.getSpecialties()).isEmpty();
	}

	// EP Tests

	@Test
	void testAddSpecialty_Valid_specialty_valid_instance() {
		Vet vet = new Vet();
		Specialty specialty = new Specialty();
		specialty.setName("Surgery");
		vet.addSpecialty(specialty);
		assertThat(vet.getNrOfSpecialties()).isEqualTo(1);
	}

	@Test
	void testAddSpecialty_Invalid_specialty_null() {
		Vet vet = new Vet();
		// HashSet.add(null) is valid - should not throw
		vet.addSpecialty(null);
		assertThat(vet.getNrOfSpecialties()).isEqualTo(1);
	}

}
