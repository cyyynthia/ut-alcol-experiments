/*
 * Copyright (c) Cynthia Rey, All rights reserved.
 * SPDX-License-Identifier: MPL-2.0
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.cynthia.alcol.patterns

import dev.cynthia.alcol.patterns.services.ServiceA
import dev.cynthia.alcol.patterns.services.ServiceB
import dev.cynthia.alcol.patterns.services.ServiceC

// Not quite a facade as it *imposes* the exposed interface to be the union sum type.
class MultiProxyComponent(
	requiredServiceA: ServiceA,
	requiredServiceB: ServiceB,
	requiredServiceC: ServiceC,
) :
	ServiceA by requiredServiceA,
	ServiceB by requiredServiceB,
	ServiceC by requiredServiceC
