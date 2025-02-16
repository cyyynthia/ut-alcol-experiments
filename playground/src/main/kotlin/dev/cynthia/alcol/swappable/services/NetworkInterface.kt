/*
 * Copyright (c) Cynthia Rey, All rights reserved.
 * SPDX-License-Identifier: MPL-2.0
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.cynthia.alcol.swappable.services

interface NetworkInterface {
	// Not very pretty but will do just fine
	fun recv(id: String): String?
	fun send(id: String, message: String)
}
