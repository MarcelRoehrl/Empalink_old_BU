package de.fraunhofer.iml.empalink.AMPDAlgorithm;

import Jama.Matrix;

public class JElfun
{
    public static Matrix sqrt(Matrix matrix)
    {
        double internal[][] = matrix.getArray();
        int row = matrix.getRowDimension();
        int col = matrix.getColumnDimension();
        Matrix result = new Matrix(row, col);
        for(int i = 0; i < row; i++)
        {
            for(int j = 0; j < col; j++)
                if(internal[i][j] < 0.0D)
                    result.set(i, j, (0.0D / 0.0D));
                else
                    result.set(i, j, Math.sqrt(internal[i][j]));

        }

        return result;
    }

    public static Matrix sum(Matrix matrix)
    {
        return sum(matrix, 1);
    }

    public static Matrix sum(Matrix matrix, int Dim)
    {
        double internal[][] = matrix.getArrayCopy();
        double temp = 0.0D;
        Dim = Math.abs(Dim);
        Dim %= 2;
        int row = matrix.getRowDimension();
        int col = matrix.getColumnDimension();
        double summing[][];
        if(Dim == 1)
        {
            summing = new double[1][col];
            for(int j = 0; j < col; j++)
            {
                for(int i = 0; i < row; i++)
                    temp += internal[i][j];

                summing[0][j] = temp;
                temp = 0.0D;
            }

        } else
        {
            summing = new double[row][1];
            for(int i = 0; i < row; i++)
            {
                for(int j = 0; j < col; j++)
                    temp += internal[i][j];

                summing[i][0] = temp;
                temp = 0.0D;
            }

        }
        return new Matrix(summing);
    }
}
