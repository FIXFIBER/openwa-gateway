<?php

declare(strict_types=1);

namespace IdaWhats\Exceptions;

/** 429 Too Many Requests — rate limited. */
class IdaWhatsRateLimitException extends IdaWhatsApiException
{
}
