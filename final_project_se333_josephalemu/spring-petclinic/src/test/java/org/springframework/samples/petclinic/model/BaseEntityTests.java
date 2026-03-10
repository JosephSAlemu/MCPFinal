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
package org.springframework.samples.petclinic.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class BaseEntityTests {

	@Test
	void testGetId_initiallyNull() {
		BaseEntity entity = new BaseEntity();
		assertThat(entity.getId()).isNull();
	}

	@Test
	void testSetId_thenGetId() {
		BaseEntity entity = new BaseEntity();
		entity.setId(42);
		assertThat(entity.getId()).isEqualTo(42);
	}

	@Test
	void testIsNew_withNullId_returnsTrue() {
		BaseEntity entity = new BaseEntity();
		assertThat(entity.isNew()).isTrue();
	}

	@Test
	void testIsNew_withNonNullId_returnsFalse() {
		BaseEntity entity = new BaseEntity();
		entity.setId(1);
		assertThat(entity.isNew()).isFalse();
	}

	// EP Tests

	@Test
	void testSetId_Valid_id_positive() {
		BaseEntity entity = new BaseEntity();
		entity.setId(1);
		assertThat(entity.getId()).isEqualTo(1);
	}

	@Test
	void testSetId_Valid_id_zero() {
		BaseEntity entity = new BaseEntity();
		entity.setId(0);
		assertThat(entity.getId()).isEqualTo(0);
	}

	@Test
	void testSetId_Valid_id_negative() {
		BaseEntity entity = new BaseEntity();
		entity.setId(-1);
		assertThat(entity.getId()).isEqualTo(-1);
	}

	@Test
	void testSetId_Invalid_id_max_overflow() {
		BaseEntity entity = new BaseEntity();
		entity.setId(Integer.MAX_VALUE);
		assertThat(entity.getId()).isEqualTo(Integer.MAX_VALUE);
	}

	@Test
	void testSetId_Invalid_id_min_underflow() {
		BaseEntity entity = new BaseEntity();
		entity.setId(Integer.MIN_VALUE);
		assertThat(entity.getId()).isEqualTo(Integer.MIN_VALUE);
	}

	@Test
	void testSetId_Invalid_id_null() {
		BaseEntity entity = new BaseEntity();
		entity.setId(1);
		entity.setId(null);
		assertThat(entity.getId()).isNull();
		assertThat(entity.isNew()).isTrue();
	}

}
