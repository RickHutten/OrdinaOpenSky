$(document).ready(function() {
    requestLoop();
})

function requestLoop() {
    requestData();
    setTimeout(requestLoop, 10 * 1000); // Call again in 10 seconds
}

function requestData() {
    $.ajax({
        url: "/all",
        success: setData
        }
    );
}

function setData(data) {
    // Show number of planes above NL
    $("#flight-count").html(data.location_count.netherlands);

    // Show top 3 countries of origin
    sorted_origin_list = [];
    for (var key in data.origin_count) {
        sorted_origin_list.push([key, data.origin_count[key]])
    }
    sorted_origin_list.sort((a, b) => b[1] - a[1])
    sorted_origin_list.forEach(function (value, i) {
        $("#top-name-"+i).html(value[0])
        $("#top-count-"+i).html(value[1])
    });

    // Show Altitude slices
    var slice_dummy = $("#slice-dummy").clone();
    slice_dummy.removeAttr('id');
    slice_dummy.removeClass("d-none")

    var plane_normal_dummy = $("#plane-dummy-normal").clone();
    plane_normal_dummy.removeAttr('id');
    plane_normal_dummy.removeClass("d-none")

    var plane_danger_dummy = $("#plane-dummy-danger").clone();
    plane_danger_dummy.removeAttr('id');
    plane_danger_dummy.removeClass("d-none")

    var slice_container = $("#altitude-slices");
    slice_container.html(""); // Empty the container

    var keys = Object.keys(data.altitudes);
    keys.sort(x => parseInt(x));
    for(var i=0; i<keys.length; ++i){
        var altitude = keys[i];

        var slice = slice_dummy.clone();
        slice.find(".altitude").html(altitude + " - " + (parseInt(altitude)+999));
        for (var j=0; j<data.altitudes[altitude].length; ++j) {
            var plane;
            var plane_id = data.altitudes[altitude][j];
            if (data.warnings.includes(plane_id)) {
                plane = plane_danger_dummy.clone();
            } else {
                plane = plane_normal_dummy.clone();
            }
            plane.html(plane_id);
            slice.find(".plane-container").append(plane);
        }
        slice_container.append(slice);
    }

    $(".toast").toast("show");
}