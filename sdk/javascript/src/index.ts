/**
 * IdaWhats JavaScript/TypeScript SDK.
 *
 * Official client library for the IdaWhats WhatsApp API Gateway.
 *
 * @example
 * ```typescript
 * import { IdaWhatsClient, IdaWhatsApiError } from '@rmyndharis/idawhats';
 *
 * const client = new IdaWhatsClient({
 *   baseUrl: 'http://localhost:2785',
 *   apiKey: 'owa_k1_…',
 * });
 *
 * await client.sessions.start('my-session');
 * const result = await client.messages.sendText('my-session', {
 *   chatId: '628123456789@c.us',
 *   text: 'Hello from the IdaWhats SDK!',
 * });
 * console.log(result.messageId);
 * ```
 *
 * @packageDocumentation
 */

export { IdaWhatsClient } from './client.js';
export { default } from './client.js';
export type { IdaWhatsClientOptions } from './client.js';
export * from './errors.js';
export type * from './types.js';
export type { ClientConfig, FetchLike, HttpMethod, RequestOptions } from './http.js';
export { buildUrl, warnIfInsecureHttpUrl } from './http.js';
