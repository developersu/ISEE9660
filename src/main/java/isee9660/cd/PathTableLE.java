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

import java.util.ArrayList;
import java.util.List;

public class PathTableLE {
    private final List<PathTableEntry> pathTableEntries;

    public PathTableLE(byte[] pathTableBytes){
        this.pathTableEntries = new ArrayList<>();

        for (int i = 0; i < pathTableBytes.length; ) {
            PathTableEntry entry = new PathTableEntry(pathTableBytes, i);
            pathTableEntries.add(entry);
            i += entry.getEntrySize();
        }
    }

    public void printDebug(){
        System.out.println("                Path Table\n ---------------------------------------------");
        for (PathTableEntry entry: pathTableEntries)
            System.out.println(
                " | Identifier Length                   :   " + entry.getIdentifierLength() + "\n" +
                " | Extended Attribute Record Length    :   " + entry.getExtendedAttributeRecordLength() + "\n" +
                " | Extent Location                     :   " + entry.getExtentLocation() + "\n" +
                " | Parent Directory Number             :   " + entry.getParentDirectoryNumber() + "\n" +
                " | Name                                :   " + entry.getDirectoryIdentifier() + "\n" +
                " | Padding byte present?               :   " + ((entry.getIdentifierLength()%2==0)?"N":"Y") +
                "\n ---------------------------------------------");
    }
}
