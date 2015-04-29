/*
 * Copyright 2013 FasterXML.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the license for the specific language governing permissions and
 * limitations under the license.
 */

package com.fasterxml.jackson.datatype.jsr310.ser;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonIntegerFormatVisitor;

import java.io.IOException;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

/**
 * Serializer for Java 8 temporal {@link YearMonth}s.
 *
 * @author Nick Williams
 * @since 2.2.0
 */
public class YearMonthSerializer extends JSR310FormattedSerializerBase<YearMonth>
{
    private static final long serialVersionUID = 1L;

    public static final YearMonthSerializer INSTANCE = new YearMonthSerializer();

    private YearMonthSerializer() 
    {
        this(null, null);
    }

    private YearMonthSerializer(Boolean useTimestamp, DateTimeFormatter dtf) 
    {
        super(YearMonth.class, useTimestamp, dtf);
    }

    @Override
    protected YearMonthSerializer withFormat(Boolean useTimestamp, DateTimeFormatter dtf) 
    {
        return new YearMonthSerializer(useTimestamp, dtf);
    }

    @Override
    public void serialize(YearMonth yearMonth, JsonGenerator generator, SerializerProvider provider) throws IOException
    {
        if (useTimestamp(provider)) {
            generator.writeStartArray();
            generator.writeNumber(yearMonth.getYear());
            generator.writeNumber(yearMonth.getMonthValue());
            generator.writeEndArray();
        } else {
            String str = (_formatter == null) ? yearMonth.toString() : yearMonth.format(_formatter);
            generator.writeString(str);
        }
    }

    @Override
    protected void _acceptTimestampVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException
    {
        JsonIntegerFormatVisitor v2 = visitor.expectIntegerFormat(typeHint);
        if (v2 != null) {
            v2.numberType(JsonParser.NumberType.LONG);
        }
    }
}
