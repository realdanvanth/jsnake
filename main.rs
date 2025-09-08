#![allow(unused_imports)]
#![allow(unused_macros)]
#![allow(dead_code)]
#![allow(non_snake_case)]
use crossterm::{ExecutableCommand,cursor,terminal};
use crossterm::terminal::enable_raw_mode;
use crossterm::terminal::disable_raw_mode;
use crossterm::event::{poll,read,KeyCode,Event};
use crossterm::style::{Print,SetBackgroundColor,SetForegroundColor,Color};
use std::io::{stdout,Write};
use::std::{time::Duration};
use rand::prelude::*;
#[macro_export]
macro_rules! eterm{
    (clear) => {stdout().execute(terminal::Clear(terminal::ClearType::All)).unwrap()}; 
    (flush) => {stdout().flush().unwrap()};
    (bold)  => {stdout().execute(SetAttribute(Attribute::Bold))};
    (size)  => {terminal::size().unwrap()};
    (raw)   => {enable_raw_mode().unwrap()};
    (unraw) => {disable_raw_mode().unwrap()};
    (blink) => {stdout().execute(cursor::EnableBlinking)};
    (dblink) => {stdout().execute(cursor::DisableBlinking)};
    (hide) => {stdout().execute(cursor::Hide).unwrap()};
    (show) => {stdout().execute(cursor::Show).unwrap()};
    (underline) => {stdout().execute(SetAttribute(Attribute::underline))};
    (move($x:expr,$y:expr))=> {stdout().execute(cursor::MoveTo($y,$x)).unwrap()};
    (color(fg,$color:ident)) =>{stdout().execute(SetForegroundColor(Color::$color)).unwrap()};
    (color(bg,$color:ident)) =>{stdout().execute(SetBackgroundColor(Color::$color)).unwrap()};
    (print($string:literal)) => {stdout().execute(Print($string.to_string())).unwrap()};
    (poll($time:expr)) => {poll(Duration::from_millis($time)).unwrap()};
}
macro_rules! input{
    ($t:ty) => {{
        let mut input = String::new();
        io::stdin().read_line(&mut input).unwrap();
        input.trim().parse::<$t>().unwrap()
    }};
}
macro_rules! string{
    ($s:literal)=> {($s).to_string();};
}
#[derive(PartialEq)]
enum Direction{
    Up,
    Down,
    Left,
    Right
}
fn main(){
    eterm!(raw);
    eterm!(clear);
    eterm!(hide); 
    let (rows,cols) = eterm!(size);
    let mut snake:Vec<(u16,u16)> = vec![(cols/2,rows/2),(cols/2,rows/2+1),(cols/2,rows/2+2)];
    let mut dir:Direction = Direction::Up; 
    let mut fx:u16 = rand::rng().random_range(0..cols); 
    let mut fy:u16 = rand::rng().random_range(0..rows);
    loop{
        if eterm!(poll(1)){
            match read().unwrap(){
                Event::Key(event)=>{
                    if event.code == KeyCode::Char('q'){
                        break;
                    }
                    else if event.code == KeyCode::Char('w'){
                        if dir != Direction::Right
                            {dir = Direction::Left;}
                    }
                    else if event.code == KeyCode::Char('d'){
                      if dir != Direction::Up{
                        dir = Direction::Down;
                      }
                    }
                    else if event.code == KeyCode::Char('a'){
                        if dir != Direction::Down{
                        dir = Direction::Up;
                        }
                    }
                    else if event.code == KeyCode::Char('s'){
                        if dir != Direction::Left{
                        dir = Direction::Right;
                        }
                    } 
                },
                _=>{continue;}
            } 
        }
        if !nextFrame(&mut snake,&dir,cols,rows,(&mut fx,&mut fy))
                        {break;}
        display(&snake,(&fx,&fy));
        eterm!(move(0,0));
        //println!("{:?}",snake);
    }
    eterm!(clear);
    eterm!(move(0,0));
    println!("game over score:{}",snake.len());
    eterm!(unraw);
    eterm!(show);
}
fn nextFrame(snake:&mut Vec<(u16,u16)>,dir:&Direction,cols:u16,rows:u16,food:(&mut u16,&mut u16))->bool{
    let (mut i,mut j):(u16,u16) = (snake[0].0,snake[0].1);
    match *dir{
        Direction::Up => {
            j = if j==0 {rows-1} else {j-1};
        },
        Direction::Down =>{
            j = (j+1)%rows;
        },
        Direction::Left =>{
            i = if i==0 {cols-1} else {i-1}
        },
        Direction::Right =>{
            i = (i+1)%cols;
        },
    }
    for (x,y) in &mut *snake{
        if *x==i&&*y==j {
            return false;
        }
    }
    snake.insert(0,(i,j));
    if  *food.0!=i||*food.1!=j 
        {
          snake.pop();
        }
    else{
          *food.0 = rand::rng().random_range(0..cols); 
          *food.1 = rand::rng().random_range(0..rows);
    }
    return true;
}
fn display(snake:&Vec<(u16,u16)>,food:(&u16,&u16)){
    eterm!(clear);
    eterm!(move(snake[0].0,snake[0].1));
    eterm!(color(fg,Blue));
    print!("@");
    eterm!(color(fg,Green));
    for i in 1..snake.len(){
        eterm!(move(snake[i].0,snake[i].1));
        print!("#");
    } 
    eterm!(move(*food.0,*food.1));
    eterm!(color(fg,Red));
    print!("*");
}
