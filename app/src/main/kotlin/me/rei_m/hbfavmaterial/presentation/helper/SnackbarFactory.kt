/*
 * Copyright (c) 2017. Rei Matsushita
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See
 * the License for the specific language governing permissions and limitations under the License.
 */

package me.rei_m.hbfavmaterial.presentation.helper

import android.support.annotation.StringRes
import android.support.design.widget.Snackbar
import android.view.View

class SnackbarFactory(private val view: View) {
    fun create(@StringRes messageId: Int): Snackbar {
        return Snackbar.make(view, view.context.getString(messageId), Snackbar.LENGTH_SHORT).setAction("Action", null)
    }
}
