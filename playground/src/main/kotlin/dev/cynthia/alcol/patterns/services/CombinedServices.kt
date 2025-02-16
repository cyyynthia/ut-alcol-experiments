/*
 * Copyright (c) Cynthia Rey, All rights reserved.
 * SPDX-License-Identifier: MPL-2.0
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.cynthia.alcol.patterns.services

interface CombinedServices {
	// Not naming them a/b/c, to make the interface structurally different to the sum type `ServiceA | ServiceB | ServiceC`.
	// Would be different anyway in the JVM, but CBSE could be implemented in a functional language... :p
	fun x()
	fun y()
	fun z()
}
