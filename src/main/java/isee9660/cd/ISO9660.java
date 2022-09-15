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

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static isee9660.DataFormatConverter.*;

public class ISO9660 {
    private final RandomAccessFile randomAccessFile;
    private byte type;
    private String identifier;
    private byte version;
    //unused1;
    private String systemIdentifier;
    private String volumeIdentifier;
    //unused2;
    private long volumeSpaceSize;
    //unused3;
    private short volumeSetSize;
    private short volumeSequenceNumber;
    private short logicalBlockSize;
    private int pathTableSize;
    private int locationOfType_LPathTable;
    private int locationOfOptionalType_LPathTable;
    private int locationOfType_MPathTable;
    private int locationOfOptionalType_MPathTable;
    private DirectoryEntry rootEntry;
    private String volumeSetIdentifier;
    private String publisherIdentifier;
    private String dataPreparerIdentifier;
    private String applicationIdentifier;
    private String CopyrightFileIdentifier;
    private String abstractFileIdentifier;
    private String bibliographicFileIdentifier;
    private String volumeCreationDateTime;
    private String volumeModificationDateTime;
    private String volumeExpirationDateTime;
    private String volumeEffectiveDateTime;
    private byte fileStructureStandardVersion;
    //unused4;
    //applicationUse;
    //reserved;
    private PathTableLE pathTableLE;
    public ISO9660(String path) throws Exception {
        this(new File(path));
    }
    public ISO9660(File iso) throws Exception {
        this.randomAccessFile = new RandomAccessFile(iso, "r");

        skipFirst16Sectors();
        readInitialDescriptor();
        readPathTableLE();
        printDebug();
    }
    private void skipFirst16Sectors() throws Exception{
        randomAccessFile.seek(2048*16);
    }
    private void readInitialDescriptor() throws Exception{
        byte[] firstPayloadDescriptor = new byte[2048];
        if (2048 != randomAccessFile.read(firstPayloadDescriptor))
            throw new Exception("Can't read first descriptor at offset of 0x10");

        this.type = firstPayloadDescriptor[0];
        this.identifier = new String(firstPayloadDescriptor, 0x1, 0x5, StandardCharsets.US_ASCII);
        this.version = firstPayloadDescriptor[0x6];
        // Let's perform some basic validations
        if (type != 1)
            throw new Exception("Not supported ISO Type of "+type);
        if (! identifier.contentEquals("CD001"))
            throw new Exception("Not supported ISO Identifier of "+identifier);
        if (version != 1)
            throw new Exception("Not supported ISO Version of "+version);
        this.systemIdentifier = new String(firstPayloadDescriptor, 0x8, 0x20, StandardCharsets.US_ASCII);
        this.volumeIdentifier = new String(firstPayloadDescriptor, 0x28, 0x20, StandardCharsets.US_ASCII);;
        this.volumeSpaceSize = getLEint(firstPayloadDescriptor, 0x50);  // Both-endian; 4 bit palindrome  ; amount of data available on the CD-ROM
        this.volumeSetSize = getLEshort(firstPayloadDescriptor, 0x78);
        this.volumeSequenceNumber = getLEshort(firstPayloadDescriptor, 0x7c);
        this.logicalBlockSize = getLEshort(firstPayloadDescriptor, 0x80);
        this.pathTableSize = getLEint(firstPayloadDescriptor, 0x84);
        this.locationOfType_LPathTable = getLEint(firstPayloadDescriptor, 0x8c); // LE
        this.locationOfOptionalType_LPathTable = getLEint(firstPayloadDescriptor, 0x90); //LE
        this.locationOfType_MPathTable = getBEint(firstPayloadDescriptor, 0x94); //BE
        this.locationOfOptionalType_MPathTable = getBEint(firstPayloadDescriptor, 0x98); //BE
        this.rootEntry = new DirectoryEntry(Arrays.copyOfRange(firstPayloadDescriptor, 0x9c, 0xbe));
        this.volumeSetIdentifier = new String(firstPayloadDescriptor, 0xbe, 0x80, StandardCharsets.US_ASCII);
        this.publisherIdentifier = new String(firstPayloadDescriptor, 0x13e, 0x80, StandardCharsets.US_ASCII);
        this.dataPreparerIdentifier = new String(firstPayloadDescriptor, 0x1be, 0x80, StandardCharsets.US_ASCII);
        this.applicationIdentifier = new String(firstPayloadDescriptor, 0x23e, 0x80, StandardCharsets.US_ASCII);
        this.CopyrightFileIdentifier = new String(firstPayloadDescriptor, 0x2be, 0x25, StandardCharsets.US_ASCII);
        this.abstractFileIdentifier = new String(firstPayloadDescriptor, 0x2e3, 0x25, StandardCharsets.US_ASCII);
        this.bibliographicFileIdentifier = new String(firstPayloadDescriptor, 0x308, 0x25, StandardCharsets.US_ASCII);
        this.volumeCreationDateTime = DateTime.getPrimaryVolumeDescriptorDateTime(Arrays.copyOfRange(firstPayloadDescriptor, 813, 830)).toString();
        this.volumeModificationDateTime = DateTime.getPrimaryVolumeDescriptorDateTime(Arrays.copyOfRange(firstPayloadDescriptor, 830, 847)).toString();
        this.volumeExpirationDateTime = DateTime.getPrimaryVolumeDescriptorDateTime(Arrays.copyOfRange(firstPayloadDescriptor, 847, 864)).toString();
        this.volumeEffectiveDateTime = DateTime.getPrimaryVolumeDescriptorDateTime(Arrays.copyOfRange(firstPayloadDescriptor, 864, 881)).toString();
        this.fileStructureStandardVersion = firstPayloadDescriptor[0x371];
    }

    private void readPathTableLE() throws Exception{
        randomAccessFile.seek(2048L * locationOfType_LPathTable);
        byte[] pathTableBytes = new byte[pathTableSize];
        if (pathTableSize != randomAccessFile.read(pathTableBytes))
            throw new Exception("Can't read Path Table");

        pathTableLE = new PathTableLE(pathTableBytes);
    }

    private void getRootEntryFiles(DirectoryEntry entry) throws Exception{
        //entry.printDebug();
        randomAccessFile.seek(entry.getExtentLocation() * 2048L);
        byte[] bytes = new byte[entry.getDataSize()];
        if (entry.getDataSize() != randomAccessFile.read(bytes))
            throw new Exception("Can't look inside Root folder");

        int entryOffset = 0;
        while (entryOffset < entry.getDataSize()){
            byte entryLength = bytes[entryOffset];

            if (entryLength == 0)
                break;

            byte[] entryBytes = Arrays.copyOfRange(bytes, entryOffset, entryOffset+entryLength);
            DirectoryEntry entryIn = new DirectoryEntry(entryBytes);
            entryIn.printDebug();
            entryOffset += entryLength;
        }
    }

    private void printDebug() throws Exception{
        System.out.println(
                "Type:                                         "+type  + "\n" +
                "Identifier:                                   "+identifier  + "\n" +
                "Version:                                      "+version  + "\n" +
                "System Identifier                             " + systemIdentifier + "\n" +
                "Volume Identifier                             " + volumeIdentifier + "\n" +
                "Volume Space Size                             " + volumeSpaceSize + "\n" +
                "Volume Set Size                               " + volumeSetSize + "\n" +
                "Volume Sequence Number                        " + volumeSequenceNumber + "\n" +
                "Logical Block Size                            " + logicalBlockSize + "\n"  +
                "Path Table Size                               " + pathTableSize + "\n" +
                "Location of Type-L Path Table                 " + locationOfType_LPathTable + "\n" +
                "Location of the Optional Type-L Path Table    " + locationOfOptionalType_LPathTable + "\n" +
                "Location of Type-M Path Table                 " + locationOfType_MPathTable + "\n" +
                "Location of Optional Type-M Path Table        " + locationOfOptionalType_MPathTable);
        rootEntry.printDebug();
        System.out.println(
                "Volume Set Identifier                         " + volumeSetIdentifier + "\n" +
                        "Publisher Identifier                          " + publisherIdentifier + "\n" +
                        "Data Preparer Identifier                      " + dataPreparerIdentifier + "\n" +
                        "Application Identifier                        " + applicationIdentifier + "\n" +
                        "Copyright File Identifier                     " + CopyrightFileIdentifier + "\n" +
                        "Abstract File Identifier                      " + abstractFileIdentifier + "\n" +
                        "Bibliographic File Identifier                 " + bibliographicFileIdentifier + "\n" +
                        "Volume Creation Date and Time                 " + volumeCreationDateTime + "\n" +
                        "Volume Modification Date and Time             " + volumeModificationDateTime + "\n" +
                        "Volume Expiration Date and Time               " + volumeExpirationDateTime + "\n" +
                        "Volume Effective Date and Time                " + volumeEffectiveDateTime + "\n" +
                        "File Structure Version                        " + fileStructureStandardVersion +
                        "\n--------------------------------------------------------------------------------");

        pathTableLE.printDebug();
        System.out.println("=    Files and directories @ root     =");
        getRootEntryFiles(rootEntry);
    }
}
