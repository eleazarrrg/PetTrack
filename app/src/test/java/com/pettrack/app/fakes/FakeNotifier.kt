package com.pettrack.app.fakes

import com.pettrack.app.core.notifications.Notifier

class FakeNotifier : Notifier {
    val notified = mutableListOf<Triple<Int, String, String>>()
    override fun notify(id: Int, title: String, body: String) {
        notified.add(Triple(id, title, body))
    }
}
