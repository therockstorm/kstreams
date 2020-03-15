package dev.rocky.util

import java.util.UUID

trait ValueProvider {
  def uuid(): UUID = UUID.randomUUID()
}
