package com.pettrack.app.core.notifications

/** Posts a local system notification. Abstracted so [NotificationCenter] is JVM-testable. */
interface Notifier {
    fun notify(id: Int, title: String, body: String)
}
