import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

class HomePage extends StatefulWidget {
  const HomePage({Key? key}) : super(key: key);

  @override
  _HomePageState createState() => _HomePageState();
}

class _HomePageState extends State<HomePage> {
  var epcList = [];
  var isReading = false;
  Future<void> getRFID() async {
    MethodChannel methodChannel = MethodChannel('mainChannel');
    while(isReading){
      var resultList = await methodChannel.invokeMethod('getRFID');
      epcList = resultList;
      setState(() {});
    }
    await methodChannel.invokeMethod('stop');
  }

  @override
  Widget build(BuildContext context) {

    return Scaffold(
      body: epcList.isNotEmpty ?
      ListView.builder(
          itemCount: epcList.length,
          itemBuilder: (context,index){
          return Text(epcList[index].toString());
      }) : Center (
        child: Text('Okunan bir değer yok'),),
      floatingActionButton: FloatingActionButton.extended(onPressed: ()async{
        isReading = !isReading;
        getRFID();
        setState(() {});
      }, label: Text(
          isReading == true
              ? 'DURDUR'
              : 'RFID OKU')
       ),
    );
  }
}
