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

import org.junit.jupiter.api.Test;

class VetsTests {

	@Test
	void getVetList_initializesWhenNull() {
		Vets vets = new Vets();
		List<Vet> list = vets.getVetList();
		assertThat(list).isNotNull().isEmpty();
	}

	@Test
	void getVetList_returnsSameListOnSubsequentCalls() {
		Vets vets = new Vets();
		List<Vet> list1 = vets.getVetList();
		List<Vet> list2 = vets.getVetList();
		assertThat(list1).isSameAs(list2);
	}

	@Test
	void getVetList_supportsAddingElements() {
		Vets vets = new Vets();
		Vet vet = new Vet();
		vets.getVetList().add(vet);
		assertThat(vets.getVetList()).hasSize(1).contains(vet);
	}

	// EP Tests — getVetList null-branch (valid instance initialises list)

	@Test
	void testIf_Valid_null_valid_instance() {
		Vets vets = new Vets();
		// First call: internal list is null — should be initialised and returned
		assertThat(vets.getVetList()).isNotNull();
	}

}
