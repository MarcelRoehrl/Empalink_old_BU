package de.fraunhofer.iml.empalink.Activities.AMPDAlgorithm;

import Jama.Matrix;

public class JElmat
{
    public static double[][] convertTo2D(double x[])
    {
        double holder[][] = new double[1][];
        double temp[] = (double[])x.clone();
        holder[0] = temp;
        return holder;
    }

    public static Matrix vander(double a[], int numColumns)
            throws Exception
    {
        int arrayLength = a.length;
        int col = 0;
        double temp = 0.0D;
        if(numColumns < 0)
            throw new Exception("Vander :- Column dimensions is out-of-bound, index limits boundries is >= 0 ");
        if(numColumns == 0)
            col = arrayLength;
        else
            col = numColumns;
        Matrix vanderMatrix = new Matrix(arrayLength, col, 1.0D);
        for(int i = 0; i < arrayLength; i++)
        {
            for(int j = 0; j < col - 1; j++)
            {
                temp = Math.pow(a[i], col - 1 - j);
                vanderMatrix.set(i, j, temp);
            }

        }

        return vanderMatrix;
    }

    public static Matrix zeros(int n)
            throws ArrayIndexOutOfBoundsException
    {
        return zeros(n, n);
    }

    public static Matrix zeros(int m, int n)
            throws ArrayIndexOutOfBoundsException
    {
        if(m < 1 || n < 1)
            throw new ArrayIndexOutOfBoundsException("zeros : Index is less than 1.");
        else
            return new Matrix(m, n, 0.0D);
    }

    public static Matrix ones(int n)
            throws ArrayIndexOutOfBoundsException
    {
        return ones(n, n);
    }

    public static Matrix ones(int m, int n)
            throws ArrayIndexOutOfBoundsException
    {
        if(m < 1 || n < 1)
            throw new ArrayIndexOutOfBoundsException("ones : Index is less than 1.");
        else
            return new Matrix(m, n, 1.0D);
    }

    public static double[][] reshape(double a[][], int newrow, int newcol)
            throws Exception
    {
        int row = a.length;
        int col = a[0].length;
        int columnVectorLength = row * col;
        int count = 0;
        double columnVector[][] = toColumnVector(a);
        if(row * col != newrow * newcol || newrow < 1 || newcol < 1)
            throw new Exception("Exception : reshape - OutofBoundIndex.");
        double result[][] = new double[newrow][newcol];
        for(int i = 0; i < newrow; i++)
        {
            for(int j = 0; j < newcol; j++)
                result[i][j] = columnVector[count++][0];

        }

        return result;
    }

    public static Matrix reshape(Matrix matrix, int newrow, int newcol)
            throws Exception
    {
        int row = matrix.getRowDimension();
        int col = matrix.getColumnDimension();
        int columnVectorLength = row * col;
        int count = 0;
        Matrix columnVector = toColumnVector(matrix);
        if(row * col != newrow * newcol || newrow < 1 || newcol < 1)
            throw new Exception("Exception : reshape - OutofBoundIndex.");
        Matrix result = new Matrix(newrow, newcol);
        for(int i = 0; i < newrow; i++)
        {
            for(int j = 0; j < newcol; j++)
                result.set(i, j, columnVector.get(count++, 0));

        }

        return result;
    }

    public static double[][] toColumnVector(double a[][])
    {
        int row = a.length;
        int col = a[0].length;
        double result[][];
        if(col == 1)
        {
            result = (double[][])a.clone();
        } else
        {
            result = new double[row][1];
            int count = 0;
            for(int j = 0; j < col; j++)
            {
                for(int i = 0; i < row; i++)
                    result[count++][0] = a[i][j];

            }

        }
        return result;
    }

    public static Matrix toColumnVector(Matrix matrix)
    {
        double a[] = matrix.getColumnPackedCopy();
        int row = a.length;
        double result[][] = new double[1][];
        result[0] = a;
        Matrix matResult = new Matrix(result);
        return matResult.transpose();
    }

    public static double[][] repmat(double a[][], int m, int n)
            throws Exception
    {
        int row = a.length;
        int col = a[0].length;
        int countRow = 0;
        int countColumn = 0;
        if(m < 1 || n < 1)
            throw new Exception("Repmat :- Index should be at least 1.");
        int newRowDim = row * m;
        int newColDim = col * n;
        double result[][] = new double[newRowDim][];
        double tempHolder[] = new double[newColDim];
        for(int i = 0; i < newRowDim; i++)
        {
            for(int j = 0; j < newColDim; j++)
            {
                tempHolder[j] = a[countRow][countColumn++];
                if(countColumn == col)
                    countColumn = 0;
            }

            if(++countRow == row)
                countRow = 0;
            result[i] = tempHolder;
            tempHolder = new double[newColDim];
        }

        return result;
    }

    public static Matrix repmat(Matrix matrix, int m, int n)
            throws Exception
    {
        double a[][] = matrix.getArrayCopy();
        int row = a.length;
        int col = a[0].length;
        int countRow = 0;
        int countColumn = 0;
        if(m < 1 || n < 1)
            throw new Exception("Repmat :- Index should be at least 1.");
        int newRowDim = row * m;
        int newColDim = col * n;
        double result[][] = new double[newRowDim][];
        double tempHolder[] = new double[newColDim];
        for(int i = 0; i < newRowDim; i++)
        {
            for(int j = 0; j < newColDim; j++)
            {
                tempHolder[j] = a[countRow][countColumn++];
                if(countColumn == col)
                    countColumn = 0;
            }

            if(++countRow == row)
                countRow = 0;
            result[i] = tempHolder;
            tempHolder = new double[newColDim];
        }

        return new Matrix(result);
    }
}
