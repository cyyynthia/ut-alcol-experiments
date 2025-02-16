/*
 * Copyright (c) Cynthia Rey, All rights reserved.
 * SPDX-License-Identifier: MPL-2.0
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.cynthia.alcol.swappable

import dev.cynthia.alcol.swappable.services.NetworkInterface
import java.util.LinkedList
import java.util.Queue

class WirelessNetwork : NetworkInterface {
	private val messages: MutableMap<String, Queue<String>> = mutableMapOf()

	override fun recv(id: String): String? {
		println("WirelessNetwork: recv $id")
		return messages[id]?.poll()
	}

	override fun send(id: String, message: String) {
		println("WirelessNetwork: send $id :: $message")
		messages.getOrPut(id, ::LinkedList).add(message)
	}
}
