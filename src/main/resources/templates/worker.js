var worker = document.getElementById("new-worker");
var workerlist = document.getElementById("worker-list");
var ctr = 0;
// var button = document.getElementById("add-subject-button");

function func(){

    workerlist.style.display="none";
    if(ctr==0){
    worker.style.visibility = "visible";
    ctr++;}
    else{
        worker.style.visibility = "hidden";
        workerlist.style.display="block";
        ctr = 0;
    }
}

function hide(){
   
    worker.style.visibility = "hidden";
    
}

var searchbar=document.getElementById("searchbar")
var stat_table=document.getElementById("status");

function tohide(){
    // table.style.display="inline";
      stat_table.style.visibility="visible";
}