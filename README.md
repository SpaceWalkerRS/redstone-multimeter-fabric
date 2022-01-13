# redstone-multimeter-fabric
A Fabric port of NarcolepticFrog's Redstone Multimeter Mod

## The User Interface

The following graphic shows the main components of the Redstone Multimeter user interface. You can add or remove a meter by looking at a block and pressing the `Toggle Meter` keybinding (default `m`). Holding control while placing a meter makes it unmovable (unmovable meters do not render a wireframe cube). You can pause/unpause the meters by pressing the `Pause Meters` keybinding (default `n`). While paused, you can scroll forwards and backwards through time by pressing the `Step Backward` and `Step Forward` keybindings (default `,` and `.`, respectively). Holding control while stepping forwards or backwards jumps 10 game ticks at a time.

![User Interface Overview](https://raw.githubusercontent.com/NarcolepticFrog/RedstoneMultimeter/master/figures/UIOverview.png)

- Each meter gets its own row in the UI, showing the meter name and a summary of that meter's power level for the last 60 gameticks.
- Each meter also has a corresponding 'highlight' showing which block the meter is monitoring. The color of the highlight matches the color of the corresponding row.
- For pulses that last longer than 5 gameticks, the duration of the pulse is also shown textually. This number is the *number of gameticks for which the meter was powered at the start*.
- When the meters are paused, the subtick ordering of any powering/unpowering events is shown to the right of the overview. Green and red rectangles correspond to the meter becoming powered or unpowered, respectively.
- If the meter is pushed by a piston, a horizontal line is drawn in the overlay to show that the block moved.
