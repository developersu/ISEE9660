/*

     Copyright "2022" Dmitry Isaenko

     This file is part of ISEE9660.

     ISEE9660 is free software: you can redistribute it and/or modify
     it under the terms of the GNU General Public License as published by
     the Free Software Foundation, either version 3 of the License, or
     (at your option) any later version.

     ISEE9660 is distributed in the hope that it will be useful,
     but WITHOUT ANY WARRANTY; without even the implied warranty of
     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
     GNU General Public License for more details.

     You should have received a copy of the GNU General Public License
     along with ISEE9660.  If not, see <https://www.gnu.org/licenses/>.

 */
package isee9660.cd;

import java.nio.charset.StandardCharsets;

public class DateTime {
    private String dateTime;
    private byte[] rawDateTime;

    private DateTime(){}

    public static DateTime getPrimaryVolumeDescriptorDateTime(byte[] dateTimeBytes){
        DateTime INSTANCE = new DateTime();
        INSTANCE.rawDateTime = dateTimeBytes;

        int timeZone = dateTimeBytes[6]*15/60;

        String timeZoneString;
        if (timeZone > 0)
            timeZoneString = "+"+timeZone;
        else
            timeZoneString = Integer.toString(timeZone);

        INSTANCE.dateTime = String.format("%s-%s-%s %s:%s:%s:%s GMT%s",
                new String(dateTimeBytes, 0, 4, StandardCharsets.US_ASCII),
                new String(dateTimeBytes, 4, 2, StandardCharsets.US_ASCII),
                new String(dateTimeBytes, 6, 2, StandardCharsets.US_ASCII),
                new String(dateTimeBytes, 8, 2, StandardCharsets.US_ASCII),
                new String(dateTimeBytes, 10, 2, StandardCharsets.US_ASCII),
                new String(dateTimeBytes, 12, 2, StandardCharsets.US_ASCII),
                new String(dateTimeBytes, 14, 2, StandardCharsets.US_ASCII),
                timeZoneString);
        return INSTANCE;
    }

    public static DateTime getDateTime(byte[] dateTimeBytes){
        DateTime INSTANCE = new DateTime();
        INSTANCE.rawDateTime = dateTimeBytes;

        int timeZone = dateTimeBytes[6]*15/60;

        String timeZoneString;
        if (timeZone > 0)
            timeZoneString = "+"+timeZone;
        else
            timeZoneString = Integer.toString(timeZone);

        INSTANCE.dateTime = String.format("%d-%02d-%02d %02d:%02d:%02d GMT%s",
                (1900+dateTimeBytes[0]),
                dateTimeBytes[1],
                dateTimeBytes[2],
                dateTimeBytes[3],
                dateTimeBytes[4],
                dateTimeBytes[5],
                timeZoneString);
        return INSTANCE;
    }
    @Override
    public String toString() {return dateTime;}
    public byte[] getRawBytes() {return rawDateTime;}
}
