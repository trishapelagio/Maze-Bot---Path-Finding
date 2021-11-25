public class Maze implements Cloneable {
    private Space[][] grid;
    private Space start;
    private Space end;
    private Space currentSpace;
    public int dimension;
    public char[] priority = {'S', 'E', 'W', 'N'}; // These can be further specified using setPriority

    // Constructor 1 : Used for a clean run in the game
    public Maze(int dimension, Space start, Space end) {
        this.grid = new Space[dimension][dimension];
        this.dimension = dimension;

        for(int i = 0; i < dimension; i++) {
            for(int j = 0; j < dimension; j++) {
                this.grid[i][j] = new Space(i, j, " ");
            }
        }

        this.assignMazeToGridSquares();
        this.end = end;
        this.start = start;
        this.currentSpace = this.getStart();
    }

    // Constructor 2 : Used for a specific game state
    public Maze(Space[][] grid, Space start, Space end, Space currentSpace, int dimension) {
        this.grid = grid;
        this.start = start;
        this.end = end;
        this.currentSpace = currentSpace;
        this.dimension = dimension;
    }


    // Getters and Setters
    public Space getStart() {
        return start;
    }

    public Space getEnd() {
        return end;
    }

    public Space getCurrentSpace() {
        return this.currentSpace;
    }

    public void setStart(Space start) {
        this.start = start;
        this.grid[start.getY()][start.getX()] = start;
    }


    public void setEnd(Space end) {
        this.end = end;
        this.grid[end.getY()][end.getX()] = end;
    }

    public void setMazeWall(int y, int x) {
        this.grid[y][x].setWall();
    }

    public void setCurrentSquare(Space space) {
        this.currentSpace = space;
    }

    public void setNextSpace(Space space) { // The function is used to move the character along the maze
        this.grid[this.currentSpace.getY()][this.currentSpace.getX()].setAttribute("*");
        this.currentSpace = space;
    }

    public void assignMazeToGridSquares() { // The function assigns the current maze object to each square in the grid
        for(int i = 0; i < dimension; i++){
            for(int j = 0; j < dimension; j++) {
                this.grid[i][j].assignMaze(this);
            }
        }
    }

    public void initMaze() { // Initializes the map/maze for a new path/run
        this.resetGrid();

        this.currentSpace = this.getStart();
    }

    public void resetGrid() { // Each square has its attribute change to the original state
        for(int i = 0; i < dimension; i++) {
            for(int j = 0; j < dimension; j++) {
                if(this.grid[i][j].getAttribute() == "*") // * represents closed
                    this.grid[i][j].setAttribute(" "); // a space represents open space or traversable node
            }
        }
    }

    public void setPriority(char[] newPriority){ // Used to implement a different move priority
        if(newPriority.length == 4) {
            this.priority = newPriority;
        }
    }

    public Space[][] getGrid() {
        return grid;
    }

    public String printMaze() // For testing
    {
        String res = "   ";
        String res_under = "";
        Space temp = null;
        Space templineunder = null;
        Space tempnextcol = null;
        Space tempdiag = null;

        //Columns numbers
        for(int i = 0; i < dimension; i++)
        {
            if(i < 10)
                res += "  " + i + " ";
            else
                res += "  " + i;
        }
        res += "\n   ╔";

        //First row : Maze top edge
        for(int i = 1; i < this.dimension; i++)
        {
            temp = this.grid[0][i - 1];
            tempnextcol = this.grid[0][i];
            if(temp.isWall())
                res += "═══╤";
            else
            if(tempnextcol.isWall())
                res += "═══╤";
            else
                res += "════";
        }
        res += "═══╗\n";

        //Browse all squares
        // res = the line containing the square states
        // res_under = the graphics under the squares line with the corner unicode characters
        // contatenation of res + res_under at each line
        //Example :
        //		│   │   │   │ <- res
        //		└───┼───┼───┘ <- res_under
        //		    │   │     <- res
        //		    └───┘     <- res_under
        //		etc...
        for(int l = 0; l < this.dimension; l++)
        {
            res_under = "";
            for(int c = 0; c < this.dimension; c++)
            {
                //Get Squares
                temp = this.grid[l][c]; // = A -> Current square
                tempnextcol = temp.getE(); // = B -> Square at the right of temp
                templineunder = temp.getS(); // = C -> Square below temp
                if(l < this.dimension - 1 && c < this.dimension - 1)
                    tempdiag = templineunder.getE(); // = D -> Square in the temp lower right-hand diagonal

                if(c == 0) //First colomn of current line l
                {
                    if(l < 10)
                        res += l + "  ║";
                    else
                        res += l + " ║";

                    if(templineunder != null)
                    {
                        if(temp.isWall() || templineunder.isWall())
                            res_under += "   ╟";
                        else
                            res_under += "   ║";
                    }
                }

                if(temp.isWall())
                {
                    res += "   ";
                    res_under += "───";
                }
                else
                {
                    if(temp.getY() == this.currentSpace.getY() && temp.getX() == this.currentSpace.getX())
                        res += " o ";
                    else if (temp.getY() == this.start.getY() && temp.getX() == this.start.getX())
                        res += " S ";
                    else if (temp.getY() == this.end.getY() && temp.getX() == this.end.getX())
                        res += " E ";
                    else
                        res += " " + temp.getAttribute() + " ";


                    if(l < this.dimension - 1)
                    {
                        if(templineunder.isWall())
                            res_under += "───";
                        else
                            res_under += "   ";
                    }
                }

                //Maze right edge
                if(c == this.dimension - 1)
                {
                    res += "║";
                    if(temp != null && templineunder != null)
                    {
                        if(temp.isWall() || templineunder.isWall())
                            res_under += "╢";
                        else
                            res_under += "║";
                    }
                }
                else
                {
                    //Squares corners.
                    // two cases : wall square or not
                    if(temp.isWall())
                    {
                        res += "│";
                        if(templineunder != null && tempdiag != null && tempnextcol != null)
                        {
                            //"┼" = (B + D).(C + D) -> The most reccurent corner to write
                            if((tempnextcol.isWall() || tempdiag.isWall()) && (templineunder.isWall() || tempdiag.isWall()))
                                res_under += "┼";
                            else
                            {
                                if(!templineunder.isWall() && !tempdiag.isWall() && !tempnextcol.isWall()) //Wall on top left only
                                    res_under += "┘";
                                else if(!templineunder.isWall() && !tempdiag.isWall() && tempnextcol.isWall()) // Walls on top
                                    res_under += "┴";
                                else if(templineunder.isWall() && !tempdiag.isWall() && !tempnextcol.isWall()) // Walls on left
                                    res_under += "┤";
                            }
                        }
                    }
                    else
                    {
                        if(tempnextcol != null)
                        {
                            if(tempnextcol.isWall())
                                res += "│";
                            else
                                res += " ";

                            if(templineunder != null && tempdiag != null)
                            {
                                //"┼" = (C).(D) -> The most reccurent corner to write
                                if(templineunder.isWall() && tempnextcol.isWall())
                                    res_under += "┼";
                                else
                                {
                                    if(!templineunder.isWall() && !tempdiag.isWall() && !tempnextcol.isWall()) //No wall
                                        res_under += " ";
                                    else if(!templineunder.isWall() && tempdiag.isWall() && !tempnextcol.isWall()) //Wall on right below
                                        res_under += "┌";
                                    else if(templineunder.isWall() && !tempdiag.isWall() && !tempnextcol.isWall()) //Wall on left below
                                        res_under += "┐";
                                    else if(!templineunder.isWall() && !tempdiag.isWall() && tempnextcol.isWall()) //Wall on top right
                                        res_under += "└";
                                    else if(!templineunder.isWall() && tempdiag.isWall() && tempnextcol.isWall()) //Walls on right
                                        res_under += "├";
                                    else if(templineunder.isWall() && tempdiag.isWall() && !tempnextcol.isWall()) //Walls below
                                        res_under += "┬";
                                }
                            }
                        }
                    }
                }
            }//<- for each column
            if(l < this.dimension - 1)
                res += "\n" + res_under + "\n"; //Concatenate res + res_under
            else
            {
                //Maze bottom edge
                res += "\n   ╚";
                for(int i = 1; i < this.dimension; i++)
                {
                    temp = this.getGrid()[l][i - 1];
                    if(temp.getE().isWall() || temp.isWall())
                        res += "═══╧";
                    else
                        res += "════";
                }
                res += "═══╝\n";
            }
        }

		/*//Affichage simple
		String res = "   ";
		for(int i = 0; i < cMax; i++)
		{
			if(i >= 9)
				res += " " + i;
			else
				res += " " + i + " ";
		}
		res += "\n";
		for(int i = 0; i < this.lMax; i++)
		{
			if(i > 9)
				res += i + " ";
			else
				res += i + "  ";

			for(int j = 0; j < this.cMax; j++)
			{
				if(this.getGrid()[i][j].getAttribute() != "" && !this.getGrid()[i][j].isWall())
					res += "[" + this.getGrid()[i][j].getAttribute() + "]";
				else
					res += "[■]";
			}
			res += "\n";
		}*/
        return res;
    }

    @Override
    public Maze clone() {
        Maze foo;
        try {
            foo = (Maze)super.clone();
        }
        catch (CloneNotSupportedException e)
        {
            throw new Error();
        }
        return foo;
    }

}
