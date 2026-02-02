import java.util.List;

/**
 * From the given sorted Array, identify all the pair of integers whose sum is equal to the target value.
 * input : Array : [1,2,3,4,5,6,9,10], Target = 11.
 * Output : (1,10); (2,9); (5,6)
 */

public class Main {
    public static void main(String[] args) {
        List<Integer> arr = List.of(1, 2, 3, 4, 5, 6, 9, 10);
        int target = 11;
        findPairs(arr, target);
    }

    public static Integer findPairs(List<Integer> arr, int target) {
        if(arr == null || arr.size() < 2) {
            new InvalidInputException("Input array is invalid or too small.");
            return null;
        }

        if(target < arr.get(0) + arr.get(1) || target > arr.get(arr.size() - 1) + arr.get(arr.size() - 2)) {
            new InvalidInputException("No pairs exist for the given target value.");
            return null;
        }

        int left = 0;
        int right = arr.size() - 1;

        for(; left < right; ) {
            int sum = arr.get(left) + arr.get(right);
            if(sum == target) {
                System.out.println("(" + arr.get(left) + "," + arr.get(right) + ")");
                left++;
                right--;
            } else if(sum < target) {
                left++;
            } else {
                right--;
            }
        }

        return null;
    }

    public static class InvalidInputException extends RuntimeException {
        public InvalidInputException(String message) {
            super(message);
        }
    }
}

/**
 * Orders(order_id, customer_id, order_date, order_amount),
 * write a SQL query to find the total order amount for each customer in the last 30 days.
 */
SELECT customer_id, SUM(order_amount) AS total_order_amount FROM Orders
WHERE order_date >= NOW() - INTERVAL 30 DAY
GROUP BY customer_id;