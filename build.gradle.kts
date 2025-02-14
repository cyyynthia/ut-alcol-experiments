/*
 * Copyright (c) Cynthia Rey, All rights reserved.
 * SPDX-License-Identifier: MPL-2.0
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

plugins {
	id("java")
	id("idea")

	kotlin("jvm") version libs.versions.kotlin apply false
}

subprojects {
	group = "dev.cynthia.alcol"

	repositories {
		mavenCentral()
	}
}

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
		sourceCompatibility = JavaVersion.VERSION_17
		targetCompatibility = JavaVersion.VERSION_17
	}
}
