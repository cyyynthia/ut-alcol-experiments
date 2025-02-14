/*
 * Copyright (c) Cynthia Rey, All rights reserved.
 * SPDX-License-Identifier: MPL-2.0
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.cynthia.alcol.simple

class Assembly : Runnable {
	// Parts
	private val server = Server()
	private val client = Client(server)

	// Bind provided to internal parts
	override fun run() = client.run()
}
