//keeping page in a var
var page = 1;


async function getAllIdentities(page)  //it says get all but it really only gets by page number - 15 per page (see identity service class)
{
    if(page < 1 || Number.isNaN(page))
    {
        page = 1;
    }
    
    const url = PluginHelper.getPluginRestUrl("identity/all/" + page);
    const newHeaders = new Headers();
    newHeaders.append("X-XSRF-TOKEN", PluginHelper.getCsrfToken());

    const options = 
    {
          method: "GET"
        , headers: newHeaders
        , redirect: "follow"
    };

    const response = await fetch(url, options);

    const responseJSON = await response.json();

    document.getElementById('table-body').innerHTML = '';

    for(let identity of responseJSON)
    {
        let row = document.createElement('tr');
        row.innerHTML = '<td style="border: 1px solid black; padding: 8px;">' + identity.id 
        + '</td><td style="border: 1px solid black; padding: 8px;">' + identity.sptName 
        + '</td><td style="border: 1px solid black; padding: 8px;">' + identity.sptId + '</td>';
        
        document.getElementById('table-body').appendChild(row);
    }
}


function nextPage()
{
    let table = document.getElementById('table-body');
    let rowCount = table.rows.length;

    if(rowCount > 1)
    {
        page ++;
        document.getElementById('pg').textContent = page;
        getAllIdentities(page);
    }
}

function prevPage()
{
    if(page > 1)
    {
        page --;
        document.getElementById('pg').textContent = page;
        getAllIdentities(page);
    }
}


getAllIdentities(page);