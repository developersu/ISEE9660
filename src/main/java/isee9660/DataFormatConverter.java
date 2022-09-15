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
package isee9660;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class DataFormatConverter {
    public static short getLEshort(byte[] bytes, int fromOffset){
        return ByteBuffer.wrap(bytes, fromOffset, 0x2).order(ByteOrder.LITTLE_ENDIAN).getShort();
    }
    public static short getBEshort(byte[] bytes, int fromOffset){
        return ByteBuffer.wrap(bytes, fromOffset, 0x2).order(ByteOrder.BIG_ENDIAN).getShort();
    }

    public static int getLEint(byte[] bytes, int fromOffset){
        return ByteBuffer.wrap(bytes, fromOffset, 0x4).order(ByteOrder.LITTLE_ENDIAN).getInt();
    }
    public static int getBEint(byte[] bytes, int fromOffset){
        return ByteBuffer.wrap(bytes, fromOffset, 0x4).order(ByteOrder.BIG_ENDIAN).getInt();
    }
}
