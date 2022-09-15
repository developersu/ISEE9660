/*
    Copyright 2022 Dmitry Isaenko
     
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
import java.util.Arrays;

import static isee9660.DataFormatConverter.getLEint;
import static isee9660.DataFormatConverter.getLEshort;

public class DirectoryEntry {
    private final byte[] entryBytes;

    private final byte length;
    private final byte extendedAttributeRecordLength;
    private final int extentLocation;   // lsb start
    private final int dataSize;
    private final String dateTime;
    private final byte flags;
    private final byte fileUnitSize;
    private final byte interleave;
    private final short volumeSequenceNumber;
    private final byte identifierLength;
    private final String identifier;
    private final boolean endPaddingPresent;

    public DirectoryEntry(byte[] entryBytes){
        this.entryBytes = entryBytes;

        this.length = entryBytes[0];
        this.extendedAttributeRecordLength = entryBytes[1];
        this.extentLocation = getLEint(entryBytes, 0x2);
        this.dataSize = getLEint(entryBytes, 0xA);
        this.dateTime = DateTime.getDateTime(Arrays.copyOfRange(entryBytes, 0x12, 0x19)).toString();
        this.flags = entryBytes[0x19];
        this.fileUnitSize = entryBytes[0x1a];
        this.interleave = entryBytes[0x1b];
        this.volumeSequenceNumber = getLEshort(entryBytes, 0x1c);
        this.identifierLength = entryBytes[0x20];
        this.identifier = new String(entryBytes, 0x21, identifierLength, StandardCharsets.US_ASCII);
        this.endPaddingPresent = identifierLength % 2 == 0;
    }

    public byte getLength() {return length;}
    public byte getExtendedAttributeRecordLength() {return extendedAttributeRecordLength;}
    public int getExtentLocation() {return extentLocation;}
    public int getDataSize() {return dataSize;}
    public String getDateTime() {return dateTime;}
    public byte getFlags() {return flags;}
    public byte getFileUnitSize() {return fileUnitSize;}
    public byte getInterleave() {return interleave;}
    public short getVolumeSequenceNumber() {return volumeSequenceNumber;}
    public byte getIdentifierLength() {return identifierLength;}
    public String getIdentifier() {return identifier;}
    public boolean isEndPaddingPresent() {return endPaddingPresent;}

    public void printDebug(){
        System.out.println(
         "  Length                             " + length + "\n" +
         "  Extended Attribute Record Length   " + extendedAttributeRecordLength + "\n" +
         "  Extent Location                    " + extentLocation + "\n" +
         "  Data Size                          " + dataSize + "\n" +
         "  DateTime                           " + dateTime + "\n" +
         "  Flags [7 <- 0]                     " + String.format("%8s", Integer.toBinaryString(flags)).replace(' ', '0') + "\n" +
         "  File Unit Size                     " + fileUnitSize + "\n" +
         "  Interleave                         " + interleave + "\n" +
         "  Volume Sequence Number             " + volumeSequenceNumber + "\n" +
         "  Name Length                        " + identifierLength + "\n" +
         "  Name                               " + identifier +
         ((identifierLength == 1 && entryBytes[0x21] == 0x00)?" (stands for \".\")":"")
         +
         ((identifierLength == 1 && entryBytes[0x21] == 0x01)?"  (stands for \"..\")":"")
                 + "\n" +
         "  Is end padding present?            " + endPaddingPresent+ "\n                ---------------" );
    }
}
