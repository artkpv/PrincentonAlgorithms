public class Subset {
   public static void main(String[] args)
   {
	   if(args.length != 1)
	   {
		   StdOut.println(" usage: Subset k \n where k is number of subset items");
		   return;
	   }

	   int k = Integer.parseInt(args[0]);

	   RandomizedQueue<String> q = new RandomizedQueue<String>();
	   while(!StdIn.isEmpty())
	   {
		   String item = StdIn.readString();
		   q.enqueue(item);
	   }

	   for(int i = 0; i < k; i++)
	   {
		   StdOut.println(q.dequeue());
	   }
   }
}
