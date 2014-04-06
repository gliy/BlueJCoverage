package base;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import lombok.AllArgsConstructor;
import bluej.codecoverage.utils.join.BCoverage;
import bluej.codecoverage.utils.join.BCoverageClass;
import bluej.codecoverage.utils.join.BCoverageClass.BCoverageMethod;
import bluej.codecoverage.utils.join.BCoveragePackage;
import bluej.codecoverage.utils.serial.CoverageClass;
import bluej.codecoverage.utils.serial.CoverageMethod;
import bluej.codecoverage.utils.serial.CoveragePackage;

public class BValidator {

   private void validate(BCoverageClass bClass) {
      CoverageClass source = bClass.getSource();
      assertEquals(source.getFirstLine(), bClass.getFirstLine());
      assertEquals(source.getLastLine(), bClass.getLastLine());
      assertEquals(source.getName(), bClass.getName());
      validate(bClass.getNodes().toArray(new BCoverage[0]));

   }

   private void validate(BCoverageMethod bMethod) {
      CoverageMethod source = bMethod.getSource();

      assertEquals(source.getFirstLine(), bMethod.getFirstLine());
      assertEquals(source.getLastLine(), bMethod.getLastLine());
      validate(bMethod.getNodes().toArray(new BCoverage[0]));
   }

   private void validate(BCoveragePackage bPackage) {
      CoveragePackage source = bPackage.getSource();
      assertEquals(source.getName(), bPackage.getName());
      validate(bPackage.getNodes().toArray(new BCoverage[0]));
   }

   public BValidator validate(BCoverage<?>... bCoverages) {
      for (BCoverage<?> bCoverage : bCoverages) {
         if (bCoverage instanceof BCoverageClass) {
            validate((BCoverageClass) bCoverage);

         } else if (bCoverage instanceof BCoverageMethod) {
            validate((BCoverageMethod) bCoverage);

         } else if (bCoverage instanceof BCoveragePackage) {
            validate((BCoveragePackage) bCoverage);

         } else {
            throw new RuntimeException("Uknown subclass of BCoverage: "
                  + bCoverage.getClass());
         }
      }
      return this;
   }
}
