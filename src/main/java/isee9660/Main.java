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
package isee9660;

import isee9660.cd.ISO9660;

public class Main {
    public static void main(String[] args) throws Exception{
        if (args.length != 1){
            System.out.println("Usage: java -jar thisApplication.jar </path/to/cd.iso>");
            return;
        }
        new ISO9660(args[0]);
    }
}
