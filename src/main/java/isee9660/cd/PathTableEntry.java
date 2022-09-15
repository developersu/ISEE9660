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

import static isee9660.DataFormatConverter.getLEint;
import static isee9660.DataFormatConverter.getLEshort;

public class PathTableEntry {
    private final byte identifierLength;    // identifier aka name
    private final byte extendedAttributeRecordLength;
    private final int extentLocation;       //First logical block where directory stored
    private final short parentDirectoryNumber;
    private final String directoryIdentifier;

    private final int entrySize;

    public PathTableEntry(byte[] pathTableBytes, int offset){
        this.identifierLength = pathTableBytes[offset];
        this.extendedAttributeRecordLength = pathTableBytes[1+offset];
        this.extentLocation = getLEint(pathTableBytes, 0x2+offset);
        this.parentDirectoryNumber = getLEshort(pathTableBytes, 0x6+offset);
        this.directoryIdentifier = new String(pathTableBytes, 0x8+offset, identifierLength, StandardCharsets.US_ASCII);
        if (identifierLength % 2 == 0)
            entrySize = 8+identifierLength;
        else
            entrySize = 8+identifierLength+1; // padding bytes in the end presented

    }
    public byte getIdentifierLength() {return identifierLength;}
    public byte getExtendedAttributeRecordLength() {return extendedAttributeRecordLength;}
    public int getExtentLocation() {return extentLocation;}
    public short getParentDirectoryNumber() {return parentDirectoryNumber;}
    public String getDirectoryIdentifier() {return directoryIdentifier;}

    public int getEntrySize() {return entrySize;}
}
