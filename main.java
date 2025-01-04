import java.io.*;
import java.util.Random;
public class jsnake {
    int length;
    int timeout;
    int rows;
    int cols;
    int grid[][];
    jsnake(int length,int timeout) throws InterruptedException ,IOException
    {
        this.length=length;
        this.timeout=-1*timeout;
        get_term_info();
        this.grid=new int[rows-1][cols]; 
    }

    public void get_term_info() throws InterruptedException ,IOException
    {
        System.out.print("\033[H\033[2J");            
        System.out.flush();
        InputStreamReader ir;
        BufferedReader br;
        ProcessBuilder pb = new ProcessBuilder("tput","cols");
        pb.redirectInput(ProcessBuilder.Redirect.INHERIT);
        Process p=pb.start();
        ir=new InputStreamReader(p.getInputStream());
        br=new BufferedReader(ir);
        cols=Integer.parseInt(br.readLine());
        int exitcode=p.waitFor();
        pb = new ProcessBuilder("tput","lines");
        pb.redirectInput(ProcessBuilder.Redirect.INHERIT);
        p=pb.start();
        ir=new InputStreamReader(p.getInputStream());
        br=new BufferedReader(ir);
        rows=Integer.parseInt(br.readLine());
        exitcode=p.waitFor();
        int mini = Math.min(rows,cols);
        int grid[][]=new int[rows-1][cols];
        pb = new ProcessBuilder("sh","-c","stty raw -echo");
        pb.redirectInput(ProcessBuilder.Redirect.INHERIT);
        p=pb.start();
        System.out.print("\033[H\033[2J");
    }

    public static void main(String[] args) throws InterruptedException ,IOException {   
        jsnake inst=new jsnake(3,80);
        inst.grid[inst.rows/2][inst.cols/2]=1;
        inst.display();
        int current_key=0;
        int previous_key=0;
        Random random=new Random();
        int frequency=random.nextInt(10);
        while(current_key!='q')
        {
            System.out.print("\033[1;1H");
            Thread.sleep(80); 
            previous_key=current_key;
            if (System.in.available() > 0) {           
                current_key = System.in.read();}
            //System.out.print("current_key:"+current_key+" previous_key:"+previous_key);
            if(previous_key==115&&current_key==119)
            {
                current_key=previous_key;
                inst.play(current_key);
            }
            else if(current_key==115&&previous_key==119)
            {
                current_key=previous_key;
                inst.play(current_key);
            }
            else if(previous_key ==100 && current_key==97)
            {
                current_key=previous_key;
                inst.play(current_key);
            }
            else if( previous_key==97 && current_key==100)
            {
                current_key=previous_key;
                inst.play(current_key);
            }
            else
                inst.play(current_key);
            if(frequency==1)
            {
                inst.generatefood();
            }
            frequency=random.nextInt(10);
        }
    }

    public void clear()
    {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    public void generatefood()
    { 
        Random r= new Random();
        int x=r.nextInt(grid.length);
        int y=r.nextInt(grid[0].length);
        if(grid[x][y]==0)
        {
            grid[x][y]=timeout;
            change(x,y,grid[x][y]);
        }
    }

    public void display()
    {
        for(int i=0;i<grid.length;i++)
        {
            for(int j=0;j<grid[0].length;j++)
            {
                System.out.printf("\033[%d;%dH",i+2,j+1);
                System.out.print(" ");
            }
        }
        System.out.flush();

    }

    public void change( int i, int j, int val)
    {
        System.out.printf("\033[%d;%dH",i+2,j+1);
        if(val==1)
        {
            System.out.print("\033[34m");
            System.out.print("@");
            System.out.print("\033[0m");
        }
        else if(val<0)
        {
            System.out.print("\033[32m");
            System.out.print("*");
            System.out.print("\033[0m"); 
        }

        else if(val!=0)
        {
            System.out.print("\033[32m");
            System.out.print("#");
            System.out.print("\033[0m");
        }
        else
            System.out.print(" ");
        System.out.print("\033[1;1H");
        System.out.flush();
    }

    public int neg(int i, int x)
    {
        if(x==0)
        {
            if(i==0)
            {
                return grid.length-1;
            }
            return i-1;
        }
        else
        {
            if(i==0)
            {
                return grid[0].length-1;
            }
            return i-1;
        }
    }

    public int pos(int j, int x)
    {
        if(x==0)
        {
            if(j==grid.length-1)
            {
                return 0;
            }
            return j+1;
        }
        else
        {
            if(j==grid[0].length-1)
            {
                return 0;
            }
            return j+1;
        }
    }

    public void changetail(int x,int y)
    {
        for(int i=0;i<grid.length;i++)
        {
            for(int j=0;j<grid[0].length;j++)
            {
                if(grid[i][j]<0)
                {
                    grid[i][j]++;
                    change(i,j,grid[i][j]);
                }
                else if(grid[i][j]==length)
                {
                    grid[i][j]=0;
                    change(i,j,0);
                }
                else if(grid[i][j]!=0&&(i!=x||j!=y))
                {
                    grid[i][j]++;
                    change(i,j,grid[i][j]);
                }
            }
        }
    }

    public void play(int s)
    {
        System.out.print("SCORE : "+(length-3));
        for(int i=0;i<grid.length;i++)
        {
            for(int j=0;j<grid[0].length;j++)
            {
                if(length==0)
                    return;
                if(grid[i][j]==1&&s==115)
                {
                    if(grid[pos(i,0)][j]!=0&&grid[pos(i,0)][j]>0)
                    {
                        clear();
                        System.out.print("GAME OVER SCORE: "+length);
                        System.exit(0);
                    }
                    else if(grid[pos(i,0)][j]<0)
                    {
                        length++;
                    }
                    change(pos(i,0),j,1);
                    grid[pos(i,0)][j]=1;
                    changetail(pos(i,0),j);
                    return;
                }
                else if(grid[i][j]==1&&s==119)
                {
                    if(grid[neg(i,0)][j]!=0&&grid[neg(i,0)][j]>0)
                    {
                        clear();
                        System.out.print("GAME OVER SCORE: "+length);
                        System.exit(0);
                    }
                    else if(grid[neg(i,0)][j]<0)
                    {
                        length++;
                                            }
                    change(neg(i,0),j,1);
                    grid[neg(i,0)][j]=1;
                    changetail(neg(i,0),j);
                    return;
                }
                else if(grid[i][j]==1&&s==100)
                {
                    if(grid[i][pos(j,1)]!=0&&grid[i][pos(j,1)]>0)
                    {
                        clear();
                        System.out.print("GAME OVER SCORE: "+length);
                        System.exit(0);
                    }
                    else if(grid[i][pos(j,1)]<0)
                    {
                        length++;
                                            }
                    change(i,pos(j,1),1);
                    grid[i][pos(j,1)]=1;
                    changetail(i,pos(j,1));
                    return;
                }
                else if(grid[i][j]==1&&s==97)
                {
                    if(grid[i][neg(j,1)]!=0&&grid[i][neg(j,1)]>0)
                    {
                        clear();
                        System.out.print("GAME OVER SCORE: "+length);
                        System.exit(0);
                    }
                    else if(grid[i][neg(j,1)]<0)
                    {
                        length++;
                                            }
                    change(i,neg(j,1),1);
                    grid[i][neg(j,1)]=1;
                    changetail(i,neg(j,1));
                    return;
                }
            }
        }
    }
}


