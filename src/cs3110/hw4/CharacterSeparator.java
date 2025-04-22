package cs3110.hw4;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CharacterSeparator {
    /**
     * This method uses the WeightedAdjacencyList class to identify the space between characters in an image of text.
     * For efficiency, it should only construct a single graph object and should only make a constant
     * number of calls to Dijkstra's algorithm.
     * @param path The location of the image on disk.
     * @return Two lists of Integer. The first list indicates whitespace rows. The second list indicates whitespace columns. Returns null if some error occurred loading the image.
     *
     *@author Alex Behm
     */
	public static Pair<List<Integer>, List<Integer>> findSeparationWeighted(String path) {
	    //Minimum pixel brightness to be considered white
		final int BRIGHTNESS_THRESHOLD = 240;

	    try {
	        BitmapProcessor img = new BitmapProcessor(path);
	        int[][] pixels = img.getRGBMatrix();
	        int height = pixels.length;
	        int width = pixels[0].length;

	        // Initialize vertex list and add all pixel positions
	        List<Pair<Integer, Integer>> vertices = new ArrayList<>();
	        for (int row = 0; row < height; row++) {
	            for (int col = 0; col < width; col++) {
	                vertices.add(new Pair<>(row, col));
	            }
	        }

	        // Add a virtual source node for Dijkstra entry point
	        Pair<Integer, Integer> source = new Pair<>(-1, -1);
	        vertices.add(source);

	        WeightedAdjacencyList<Pair<Integer, Integer>> graph = new WeightedAdjacencyList<>(vertices);

	        // Build graph: add edges in four directions and connect bright edge pixels to source
	        for (int row = 0; row < height; row++) {
	            for (int col = 0; col < width; col++) {
	                Pair<Integer, Integer> current = new Pair<>(row, col);
	                int rgb = pixels[row][col];
	                int red = (rgb >> 16) & 0xFF;
	                int green = (rgb >> 8) & 0xFF;
	                int blue = rgb & 0xFF;
	                int brightness = (red + green + blue) / 3;
	                int weight = (brightness >= BRIGHTNESS_THRESHOLD) ? 0 : 1;

	                // Add edges to neighbors
	                if (row > 0) graph.addEdge(current, new Pair<>(row - 1, col), weight);
	                if (row < height - 1) graph.addEdge(current, new Pair<>(row + 1, col), weight);
	                if (col > 0) graph.addEdge(current, new Pair<>(row, col - 1), weight);
	                if (col < width - 1) graph.addEdge(current, new Pair<>(row, col + 1), weight);

	                // Connect bright edge pixels to virtual source
	                if (row == 0 || col == 0) {
	                    if (brightness >= BRIGHTNESS_THRESHOLD) {
	                        graph.addEdge(source, current, 0);
	                    }
	                }
	            }
	        }

	        // Run Dijkstra once from the virtual source
	        Map<Pair<Integer, Integer>, Long> distances = graph.getShortestPaths(source);

	        List<Integer> whitespaceRows = new ArrayList<>();
	        List<Integer> whitespaceColumns = new ArrayList<>();

	        // Check for whitespace rows: all pixels must be reachable with 0 distance
	        for (int row = 0; row < height; row++) {
	            boolean isWhitespace = true;
	            for (int col = 0; col < width; col++) {
	                Pair<Integer, Integer> p = new Pair<>(row, col);
	                int rgb = pixels[row][col];
	                int red = (rgb >> 16) & 0xFF;
	                int green = (rgb >> 8) & 0xFF;
	                int blue = rgb & 0xFF;
	                int brightness = (red + green + blue) / 3;

	                if (brightness < BRIGHTNESS_THRESHOLD || !distances.containsKey(p) || distances.get(p) > 0) {
	                    isWhitespace = false;
	                    break;
	                }
	            }
	            if (isWhitespace) whitespaceRows.add(row);
	        }

	        // Check for whitespace columns: all pixels must be reachable with 0 distance
	        for (int col = 0; col < width; col++) {
	            boolean isWhitespace = true;
	            for (int row = 0; row < height; row++) {
	                Pair<Integer, Integer> p = new Pair<>(row, col);
	                int rgb = pixels[row][col];
	                int red = (rgb >> 16) & 0xFF;
	                int green = (rgb >> 8) & 0xFF;
	                int blue = rgb & 0xFF;
	                int brightness = (red + green + blue) / 3;

	                if (brightness < BRIGHTNESS_THRESHOLD || !distances.containsKey(p) || distances.get(p) > 0) {
	                    isWhitespace = false;
	                    break;
	                }
	            }
	            if (isWhitespace) whitespaceColumns.add(col);
	        }

	        return new Pair<>(whitespaceRows, whitespaceColumns);

	    } catch (IOException e) {
	        return null;
	    }
	}
}
