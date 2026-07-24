<?php

declare(strict_types=1);

namespace IdaWhats\Exceptions;

/**
 * Raised when the API responds with a non-2xx status.
 *
 * Carries the HTTP status code and the parsed error body. Use the named
 * subclass for common statuses, or branch on getStatus().
 */
class IdaWhatsApiException extends IdaWhatsException
{
    private int $status;
    /** @var mixed */
    private $body;
    private ?string $errorKind;

    /**
     * @param mixed $body
     */
    public function __construct(string $message, int $status, $body = null, ?string $errorKind = null)
    {
        parent::__construct($message);
        $this->status = $status;
        $this->body = $body;
        $this->errorKind = $errorKind;
    }

    public function getStatus(): int
    {
        return $this->status;
    }

    /** @return mixed */
    public function getBody()
    {
        return $this->body;
    }

    public function getErrorKind(): ?string
    {
        return $this->errorKind;
    }

    /**
     * Build the most specific IdaWhatsApiException subclass for a status code.
     *
     * @param mixed $body
     */
    public static function classify(int $status, string $message, $body, ?string $errorKind): IdaWhatsApiException
    {
        return match ($status) {
            401 => new IdaWhatsAuthException($message, $status, $body, $errorKind),
            403 => new IdaWhatsForbiddenException($message, $status, $body, $errorKind),
            404 => new IdaWhatsNotFoundException($message, $status, $body, $errorKind),
            409 => new IdaWhatsConflictException($message, $status, $body, $errorKind),
            429 => new IdaWhatsRateLimitException($message, $status, $body, $errorKind),
            501 => new IdaWhatsNotImplementedException($message, $status, $body, $errorKind),
            default => new IdaWhatsApiException($message, $status, $body, $errorKind),
        };
    }
}
