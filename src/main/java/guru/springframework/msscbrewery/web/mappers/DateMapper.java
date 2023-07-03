package guru.springframework.msscbrewery.web.mappers;

import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.ZoneId;

@Component
public class DateMapper {
    public Timestamp offsetDateTime2TimeStamp(OffsetDateTime odt) {
        if(odt == null) {
            return  null;
        }
        return Timestamp.from(odt.toInstant());
    }

    public OffsetDateTime timeStamp2OffsetDateTime(Timestamp timestamp) {
        if(timestamp == null) {
            return null;
        }
        return OffsetDateTime.ofInstant(timestamp.toInstant(), ZoneId.systemDefault());
    }
}
