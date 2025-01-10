package seamfinding;

import seamfinding.energy.EnergyFunction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Dynamic programming implementation of the {@link SeamFinder} interface.
 *
 * @see SeamFinder
 */
public class DynamicProgrammingSeamFinder implements SeamFinder {

    @Override
    public List<Integer> findHorizontal(Picture picture, EnergyFunction f) {
        int width = picture.width();
        int height = picture.height();
        
        double[][] energyCost = new double[width][height];
        int[][] predecessors = new int[width][height];
        
        // Add energy values to the left
        for (int y = 0; y < height; y++) {
            energyCost[0][y] = f.apply(picture, 0, y);
        }

        // Fill in the rest of the dynamic programming table
        for (int x = 1; x < width; x++) {
            for (int y = 0; y < height; y++) {
                //left-middle
                double minEnergy = energyCost[x - 1][y]; 
                int predecessor = y;
                //left-up
                if (y > 0 && energyCost[x - 1][y - 1] < minEnergy) {
                    minEnergy = energyCost[x - 1][y - 1]; 
                    predecessor = y - 1;
                }
                 // left-down
                if (y < height - 1 && energyCost[x - 1][y + 1] < minEnergy) {
                    minEnergy = energyCost[x - 1][y + 1];
                    predecessor = y + 1;
                }
                energyCost[x][y] = f.apply(picture, x, y) + minEnergy;
                predecessors[x][y] = predecessor;
            }
        }

        List<Integer> seam = new ArrayList<>();
        int minSeamEnd = 0;
        double minSeamEnergy = energyCost[width - 1][0];

        for (int y = 1; y < height; y++) {
            if (energyCost[width - 1][y] < minSeamEnergy) {
                minSeamEnergy = energyCost[width - 1][y];
                minSeamEnd = y;
            }
        }

        int currentY = minSeamEnd;
        for (int x = width - 1; x >= 0; x--) {
            seam.add(currentY);
            currentY = predecessors[x][currentY];
        }

        Collections.reverse(seam);

        return seam;
    }
}

