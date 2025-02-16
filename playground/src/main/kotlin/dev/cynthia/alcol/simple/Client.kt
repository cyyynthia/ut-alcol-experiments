/*
 * Copyright (c) Cynthia Rey, All rights reserved.
 * SPDX-License-Identifier: MPL-2.0
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.cynthia.alcol.simple

import dev.cynthia.alcol.simple.services.Startable

class Client(
	// Requires Startable
	private val server: Startable
) : Runnable { // Provides Runnable
	override fun run() {
		println("Delegating to the provided startable")
		server.start()
	}
}
