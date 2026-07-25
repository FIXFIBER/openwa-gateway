import { ApiProperty, ApiPropertyOptional } from '@nestjs/swagger';
import {
  IsString,
  IsNotEmpty,
  IsOptional,
  IsLatitude,
  IsLongitude,
  IsBoolean,
  IsArray,
  ArrayMinSize,
  ArrayMaxSize,
  MaxLength,
  ValidateNested,
} from 'class-validator';
import { Type } from 'class-transformer';
import { ToStrictBoolean, ToStrictNumber } from '../../../common/utils/strict-boolean';

/**
 * Validated DTOs for the message action endpoints. These replaced inline
 * `@Body()` object-literal types, which erase at runtime so the global ValidationPipe had
 * no metadata to validate or whitelist against.
 */

export class SendLocationDto {
  @ApiProperty({ description: 'Chat ID (e.g. 628123456789@c.us)' })
  @IsString()
  @IsNotEmpty()
  chatId: string;

  @ApiProperty({ example: -6.2088 })
  @ToStrictNumber()
  @IsLatitude()
  latitude: number;

  @ApiProperty({ example: 106.8456 })
  @ToStrictNumber()
  @IsLongitude()
  longitude: number;

  @ApiPropertyOptional({ maxLength: 1024 })
  @IsOptional()
  @IsString()
  @MaxLength(1024)
  description?: string;

  @ApiPropertyOptional({ maxLength: 1024 })
  @IsOptional()
  @IsString()
  @MaxLength(1024)
  address?: string;
}

export class SendContactDto {
  @ApiProperty()
  @IsString()
  @IsNotEmpty()
  chatId: string;

  @ApiProperty({ maxLength: 255 })
  @IsString()
  @IsNotEmpty()
  @MaxLength(255)
  contactName: string;

  @ApiProperty({ maxLength: 30 })
  @IsString()
  @IsNotEmpty()
  @MaxLength(30)
  contactNumber: string;
}

export class SendPollDto {
  @ApiProperty({ description: 'Chat ID (e.g. 628123456789@c.us or 1203630000@g.us)' })
  @IsString()
  @IsNotEmpty()
  chatId: string;

  @ApiProperty({ description: 'Poll question / title', maxLength: 255, example: 'Where should we meet?' })
  @IsString()
  @IsNotEmpty()
  @MaxLength(255)
  name: string;

  // WhatsApp itself caps polls at 12 options and ~100 chars per option; validating here keeps the
  // failure a clean 400 instead of an engine error deep in the send path.
  @ApiProperty({
    description: 'Options to vote on (WhatsApp allows between 2 and 12)',
    type: [String],
    example: ['Park', 'Beach', 'Downtown'],
  })
  @IsArray()
  @ArrayMinSize(2)
  @ArrayMaxSize(12)
  @IsString({ each: true })
  @IsNotEmpty({ each: true })
  @MaxLength(100, { each: true })
  options: string[];

  @ApiPropertyOptional({ description: 'Allow voters to pick several options (default single choice)' })
  // Read strictly for the same reason as DeleteMessageDto.forEveryone: without it the pipe's
  // implicit conversion turns any non-empty string into `true`, and this file's own spec has always
  // asserted that a non-boolean here is rejected.
  @ToStrictBoolean()
  @IsOptional()
  @IsBoolean()
  allowMultipleAnswers?: boolean;
}

export class SendListRowDto {
  @ApiProperty({ description: 'Row title (the tappable item)', maxLength: 255 })
  @IsString()
  @IsNotEmpty()
  @MaxLength(255)
  title: string;

  @ApiPropertyOptional({ description: 'Optional row description', maxLength: 255 })
  @IsOptional()
  @IsString()
  @MaxLength(255)
  description?: string;

  @ApiPropertyOptional({ description: 'Optional unique row id' })
  @IsOptional()
  @IsString()
  rowId?: string;
}

export class SendListSectionDto {
  @ApiProperty({ description: 'Section heading', maxLength: 255 })
  @IsString()
  @IsNotEmpty()
  @MaxLength(255)
  title: string;

  @ApiPropertyOptional({ description: 'Optional section description', maxLength: 255 })
  @IsOptional()
  @IsString()
  @MaxLength(255)
  description?: string;

  @ApiProperty({ description: 'Rows in this section (at least one)', type: [SendListRowDto] })
  @IsArray()
  @ArrayMinSize(1)
  @ValidateNested({ each: true })
  @Type(() => SendListRowDto)
  rows: SendListRowDto[];
}

export class SendListDto {
  @ApiProperty({ description: 'Chat ID (e.g. 628123456789@c.us or 1203630000@g.us)' })
  @IsString()
  @IsNotEmpty()
  chatId: string;

  @ApiProperty({ description: 'Title shown in the list message header', maxLength: 255 })
  @IsString()
  @IsNotEmpty()
  @MaxLength(255)
  title: string;

  @ApiProperty({ description: 'Button label that opens the list', maxLength: 255, example: 'View menu' })
  @IsString()
  @IsNotEmpty()
  @MaxLength(255)
  buttonText: string;

  @ApiPropertyOptional({ description: 'Footer text', maxLength: 255 })
  @IsOptional()
  @IsString()
  @MaxLength(255)
  footerText?: string;

  @ApiProperty({ description: 'One or more sections; each section has a title and rows', type: [SendListSectionDto] })
  @IsArray()
  @ArrayMinSize(1)
  @ValidateNested({ each: true })
  @Type(() => SendListSectionDto)
  sections: SendListSectionDto[];
}

export class ReplyMessageDto {
  @ApiProperty()
  @IsString()
  @IsNotEmpty()
  chatId: string;

  @ApiProperty()
  @IsString()
  @IsNotEmpty()
  quotedMessageId: string;

  @ApiProperty({ maxLength: 4096 })
  @IsString()
  @IsNotEmpty()
  @MaxLength(4096)
  text: string;
}

export class ForwardMessageDto {
  @ApiProperty()
  @IsString()
  @IsNotEmpty()
  fromChatId: string;

  @ApiProperty()
  @IsString()
  @IsNotEmpty()
  toChatId: string;

  @ApiProperty()
  @IsString()
  @IsNotEmpty()
  messageId: string;
}

export class ReactMessageDto {
  @ApiProperty()
  @IsString()
  @IsNotEmpty()
  chatId: string;

  @ApiProperty()
  @IsString()
  @IsNotEmpty()
  messageId: string;

  // Empty string is VALID — it removes the reaction (endpoint contract). So @IsString, not @IsNotEmpty.
  @ApiProperty({ description: 'Emoji to react with. Send an empty string to remove the reaction.', maxLength: 32 })
  @IsString()
  @MaxLength(32)
  emoji: string;
}

export class DeleteMessageDto {
  @ApiProperty()
  @IsString()
  @IsNotEmpty()
  chatId: string;

  @ApiProperty()
  @IsString()
  @IsNotEmpty()
  messageId: string;

  @ApiPropertyOptional({ description: 'Delete for everyone (default true)' })
  // The field's only purpose is to say "no, delete locally" — the default is already true
  // (message.service.ts). Under the pipe's implicit conversion a string `"false"` would become
  // boolean `true`, turning an explicit local-only delete into an irreversible retraction from the
  // recipient's device, so the value is read strictly rather than interpreted.
  @ToStrictBoolean()
  @IsOptional()
  @IsBoolean()
  forEveryone?: boolean;
}

export class EditMessageDto {
  @ApiProperty()
  @IsString()
  @IsNotEmpty()
  chatId: string;

  @ApiProperty()
  @IsString()
  @IsNotEmpty()
  messageId: string;

  // Same body cap as SendTextMessageDto.text — an edit cannot exceed what a send allows.
  @ApiProperty({ description: 'New text body for the message', maxLength: 4096 })
  @IsString()
  @IsNotEmpty()
  @MaxLength(4096)
  body: string;
}
