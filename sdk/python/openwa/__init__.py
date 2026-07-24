"""
IdaWhats Python SDK.

Official client library for the IdaWhats WhatsApp API Gateway.

Example usage::

    from idawhats import IdaWhatsClient

    client = IdaWhatsClient(
        base_url="http://localhost:2785",
        api_key="owa_k1_…",
    )

    client.sessions.start("my-session")
    result = client.messages.send_text("my-session", {
        "chatId": "628123456789@c.us",
        "text": "Hello from the IdaWhats Python SDK!",
    })
    print(result["messageId"])
"""

from __future__ import annotations

from .client import IdaWhatsClient
from .errors import (
    IdaWhatsApiError,
    IdaWhatsAuthError,
    IdaWhatsConflictError,
    IdaWhatsError,
    IdaWhatsForbiddenError,
    IdaWhatsNotFoundError,
    IdaWhatsNotImplementedError,
    IdaWhatsRateLimitError,
    IdaWhatsTimeoutError,
)

__all__ = [
    "IdaWhatsClient",
    "IdaWhatsError",
    "IdaWhatsApiError",
    "IdaWhatsAuthError",
    "IdaWhatsForbiddenError",
    "IdaWhatsNotFoundError",
    "IdaWhatsConflictError",
    "IdaWhatsRateLimitError",
    "IdaWhatsNotImplementedError",
    "IdaWhatsTimeoutError",
]
