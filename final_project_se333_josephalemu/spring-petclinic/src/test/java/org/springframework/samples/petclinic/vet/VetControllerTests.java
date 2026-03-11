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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(VetController.class)
class VetControllerTests {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private VetRepository vetRepository;

	@Test
	void showVetList_defaultPage_returnsVetListView() throws Exception {
		Page<Vet> emptyPage = new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 5), 0);
		given(vetRepository.findAll(any(Pageable.class))).willReturn(emptyPage);

		mockMvc.perform(get("/vets.html"))
			.andExpect(status().isOk())
			.andExpect(view().name("vets/vetList"))
			.andExpect(model().attributeExists("currentPage", "totalPages", "totalItems", "listVets"));
	}

	@Test
	void showVetList_page2_returnsVetListView() throws Exception {
		Page<Vet> emptyPage = new PageImpl<>(Collections.emptyList(), PageRequest.of(1, 5), 0);
		given(vetRepository.findAll(any(Pageable.class))).willReturn(emptyPage);

		mockMvc.perform(get("/vets.html").param("page", "2"))
			.andExpect(status().isOk())
			.andExpect(view().name("vets/vetList"));
	}

	@Test
	void showResourcesVetList_returnsOkWithVetsJson() throws Exception {
		given(vetRepository.findAll()).willReturn(Collections.emptyList());

		mockMvc.perform(get("/vets").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
	}

	// EP Tests based on MCP tool

	@Test
	void testAddPaginationModel_Valid_page_positive() throws Exception {
		Page<Vet> page = new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 5), 0);
		given(vetRepository.findAll(any(Pageable.class))).willReturn(page);

		mockMvc.perform(get("/vets.html").param("page", "1"))
			.andExpect(status().isOk())
			.andExpect(model().attribute("currentPage", 1));
	}

	@Test
	void testAddPaginationModel_Valid_page_second() throws Exception {
		Page<Vet> page = new PageImpl<>(Collections.emptyList(), PageRequest.of(1, 5), 12);
		given(vetRepository.findAll(any(Pageable.class))).willReturn(page);

		mockMvc.perform(get("/vets.html").param("page", "2"))
			.andExpect(status().isOk())
			.andExpect(model().attribute("currentPage", 2));
	}

}
