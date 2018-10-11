import urllib
import urllib2
import json
from flask import Flask,jsonify,render_template,make_response,request
app = Flask(__name__)

@app.route('/manufacturer')
def manufacturer():
    return make_response(render_template('manufacturer.html'),200)

@app.route('/updateNewBatch')
def updateNewBatch():
    return make_response(render_template('updateNewBatch.html'),200)

@app.route('/updateOrderStatusManufacturer')
def updateOrderStatusManufacturer():
    return make_response(render_template('updateOrderStatusManufacturer.html'),200)
	

@app.route('/search')
def search():
    return make_response(render_template('search.html'),200)
	
@app.route('/updateOrderStatusDistributor')
def updateOrderStatusDistributor():
    return make_response(render_template('updateOrderStatusDistributor.html'),200)
	
@app.route('/placeOrder')
def placeOrder():
    return make_response(render_template('placeOrder.html'),200)

	
@app.route('/temperatureHistory')
def temperatureHistory():
    return make_response(render_template('temperatureHistory.html'),200)
	

@app.route('/updateOrderStatusPharma')
def updateOrderStatusPharma():
    return make_response(render_template('updateOrderStatusPharma.html'),200)
	
    @app.route('/searchOrder')
def searchOrder():
    return make_response(render_template('searchOrder.html'),200)
	
    @app.route('/reportProblem')
def reportProblem():
    return make_response(render_template('reportProblem.html'),200)
	
    @app.route('/updateSell')
def updateSell():
    return make_response(render_template('updateSell.html'),200)
	
    	
    @app.route('/updateDrugStatus')
def updateDrugStatus():
    return make_response(render_template('updateDrugStatus.html'),200)
	
    @app.route('/')
def index():
    return make_response(render_template('index.html'),200)
