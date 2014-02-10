Usage: Drop the compiled jar into the blueJ extensions folder, 
then copy all required jar files to the user's bluej configuration folder. 
Details about the folder's location can be found on https://www.bluej.org/faq.html#faq_config.

  If the target is already attached to another class a dialog will come up and prompt you if you
  want to remove the target from its current attachment.
  
Bugs/Defects:
  When you move a target, only the moved target will be updated graphically until any class is dragged,
  then the rest of the targets along the chain will snap to the correct position
  
  Added classes and targets are not realized until the project or package is closed then reopened.
  
  This plugin only supports having 1 project open at a time.
Building:
  To build the jar from the source run the ant script with 1 property: loc set to the BlueJ installed directory
  ie(F:\BlueJ)
