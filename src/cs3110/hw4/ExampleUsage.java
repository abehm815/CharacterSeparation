package cs3110.hw4;

public class ExampleUsage {

	public static void main(String[] args) {
	//Show the before and after of the two examples in the resource folder
        for (int i = 1; i <= 2; i++) {
            String imagePath = "resources/example" + i + ".bmp";
            System.out.println("Processing: " + imagePath);
            CharacterSeparator.visualizeSeparations(imagePath);
        }

	}

}
