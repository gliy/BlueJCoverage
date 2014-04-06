package bluej.codecoverage.utils.serial;

import static bluej.codecoverage.utils.serial.CoverageType.CLASS;
import static bluej.codecoverage.utils.serial.CoverageType.METHOD;
import static bluej.codecoverage.utils.serial.CoverageType.PACKAGE;
import static bluej.codecoverage.utils.serial.CoverageType.findType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import base.CoverageTestBase;

public class CoverageTypeTest extends CoverageTestBase {
   public void testFindType() throws Exception {
      assertEquals(PACKAGE, findType(new CoveragePackage(null)));
      assertEquals(METHOD, findType(new CoverageMethod(1, 1)));
      assertEquals(CLASS, findType(new CoverageClass()));

      CoverageClass enumClz = mock(CoverageClass.class);
      when(enumClz.getSuperClass()).thenReturn("java/lang/Enum");
      assertEquals(CoverageType.ENUM, findType(enumClz));
   }
}
