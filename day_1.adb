with Ada.Text_IO;
use Ada.Text_IO;
procedure Day_1 is
   Input_File : Ada.Text_IO.File_Type;
   Module     : Integer;
   Sum_1      : Integer := 0;
   Sum_2      : Integer := 0;

   function Consumption(M : in Integer) return Integer is
   begin
      return M / 3 - 2;
   end Consumption;

   function Recursive_Consumption(M: in Integer) return Integer is
      C: Integer := Consumption (M);
   begin
      if C < 0 then
         return 0;
      else
         return C + Recursive_Consumption (C);
      end if;
   end Recursive_Consumption;

begin
   Open (File => Input_File,
         Mode => Ada.Text_IO.In_File,
         Name => "input1.txt");

   while not End_Of_File (Input_File) loop
      Module := Integer'Value (Get_Line (Input_File));
      Sum_1 := Sum_1 + Consumption (Module);
      Sum_2 := Sum_2 + Recursive_Consumption (Module);
   end loop;

   Put_Line ("Part 1 : " & Sum_1'Img);
   Put_Line ("Part 2 : " & Sum_2'Img);
end Day_1;
