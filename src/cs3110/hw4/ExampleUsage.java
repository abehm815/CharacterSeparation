package cs3110.hw4;

public class ExampleUsage {

	public static void main(String[] args) {
		// Loop through test1.bmp to test10.bmp in the resources folder to visually show where it sho
        for (int i = 1; i <= 2; i++) {
            String imagePath = "resources/example" + i + ".bmp";
            System.out.println("Processing: " + imagePath);
            CharacterSeparator.visualizeSeparations(imagePath);
        }

	}

}
